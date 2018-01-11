package io.scal.ambi.model.repository.data.chat

import android.content.Context
import com.twilio.chat.CallbackListener
import com.twilio.chat.ChatClient
import com.twilio.chat.ErrorInfo
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Function
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.data.server.ChatApi
import io.scal.ambi.model.repository.local.LocalUserDataRepository
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class TwilioAuthenticationRepository @Inject constructor(context: Context,
                                                                  private val localUserDataRepository: LocalUserDataRepository,
                                                                  private val rxSchedulersAbs: RxSchedulersAbs,
                                                                  private val chatApi: ChatApi) {

    private val accessInfoObservable = BehaviorSubject.create<AccessInfo>()
    private val refreshTokenCommand = PublishSubject.create<Unit>()

    private val chatClientInfoSubject = BehaviorSubject.create<ChatClientInfo>()

    init {
        observeCurrentUserChange()
        observeRefreshTokenCommand()
        observeChatClientCreation(context)
    }

    internal fun getChatClientInfo(): Observable<ChatClientInfo> = chatClientInfoSubject
        .distinctUntilChanged()

    private fun observeCurrentUserChange() {
        localUserDataRepository.observeCurrentUser()
            .subscribeOn(rxSchedulersAbs.mainThreadScheduler)
            .observeOn(rxSchedulersAbs.computationScheduler)
            .onErrorResumeNext(Function { t ->
                Single
                    .fromCallable {
                        accessInfoObservable.onNext(AccessInfo.Nothing)
                        throw t
                    }
                    .delay(5, TimeUnit.SECONDS, true)
                    .toObservable()
            })
            .retry()
            .map { it.uid }
            .distinctUntilChanged()
            .subscribe { refreshToken() }
    }

    private fun refreshToken() {
        refreshTokenCommand.onNext(Unit)
    }

    private fun observeRefreshTokenCommand() {
        refreshTokenCommand
            .observeOn(rxSchedulersAbs.ioScheduler)
            .switchMapSingle { chatApi.generateChatAccessToken() }
            .map { it.parse() }
            .retry()
            .subscribe { accessInfoObservable.onNext(it) }

    }

    private fun observeChatClientCreation(context: Context) {
        accessInfoObservable
            .observeOn(rxSchedulersAbs.ioScheduler)
            .switchMap {
                when (it) {
                    is AccessInfo.Nothing -> Observable.just(ChatClientInfo.Error(IllegalStateException("no data to create chat")))
                    is AccessInfo.Data    ->
                        createChatInstance(context, it.accessToken)
                            .doOnError {
                                Timber.e(it, "can not initialize twilio chat client")
                                chatClientInfoSubject.onNext(ChatClientInfo.Error(it))
                            }
                            .retry()
                }
            }
            .subscribe { chatClientInfoSubject.onNext(it) }
    }

    private fun createChatInstance(context: Context, accessToken: String): Observable<ChatClientInfo> =
        Observable.create<ChatClientInfo> { e ->
            val props = ChatClient.Properties.Builder()
                .createProperties()

            ChatClient.create(context.applicationContext,
                              accessToken,
                              props,
                              object : CallbackListener<ChatClient>() {
                                  override fun onSuccess(client: ChatClient) {
                                      if (!e.isDisposed) {
                                          e.onNext(ChatClientInfo.Data(client))
                                      }
                                  }

                                  override fun onError(errorInfo: ErrorInfo) {
                                      if (!e.isDisposed) {
                                          e.onError(IllegalStateException("can not create chat. code: ${errorInfo.code}, message: ${errorInfo.message}"))
                                      }
                                  }
                              })
        }
            .doOnSubscribe {
                chatClientInfoSubject.value?.chatClient?.shutdown()
                chatClientInfoSubject.onNext(ChatClientInfo.Error(IllegalArgumentException("we are creating new chat client now")))
            }
}