package io.scal.ambi.model.repository.data.chat

import android.content.Context
import com.twilio.chat.ChatClient
import com.twilio.chat.NotificationPayload
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Function
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.data.server.ChatApi
import io.scal.ambi.model.repository.data.chat.data.AccessInfo
import io.scal.ambi.model.repository.data.chat.data.ChatClientInfo
import io.scal.ambi.model.repository.data.chat.utils.TwilioCallbackSingle
import io.scal.ambi.model.repository.local.ILocalDataRepository
import io.scal.ambi.model.repository.local.LocalUserDataRepository
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class TwilioAuthenticationRepository @Inject constructor(context: Context,
                                                                  private val localUserDataRepository: LocalUserDataRepository,
                                                                  private val localDateRepository: ILocalDataRepository,
                                                                  private val rxSchedulersAbs: RxSchedulersAbs,
                                                                  private val chatApi: ChatApi) {

    private val accessInfoObservable = BehaviorSubject.create<AccessInfo>()
    private val refreshTokenCommand = PublishSubject.create<String>()

    private val chatClientInfoSubject = BehaviorSubject.create<ChatClientInfo>()

    init {
        observeCurrentUserChange()
        observeRefreshTokenCommand()
        observeChatClientCreation(context)
    }

    internal fun getChatClientInfo(): Observable<ChatClientInfo> =
        chatClientInfoSubject.distinctUntilChanged()

    internal fun handleNotification(payload: NotificationPayload) {
        val chatSubject = chatClientInfoSubject.value
        val chatClient = chatSubject?.chatClient
        chatClient?.handleNotification(payload)
    }

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
            .subscribe { refreshToken(it) }
    }

    private fun refreshToken(userId: String) {
        refreshTokenCommand.onNext(userId)
    }

    private fun observeRefreshTokenCommand() {
        refreshTokenCommand
            .observeOn(rxSchedulersAbs.ioScheduler)
            .switchMapSingle { chatApi.generateChatAccessToken(ChatApi.AccessTokenRequest(it, localDateRepository.getDeviceUid())) }
            .map { it.parse() }
            .retry()
            .subscribe { accessInfoObservable.onNext(it) }
    }

    private fun observeChatClientCreation(context: Context) {
        accessInfoObservable
            .observeOn(rxSchedulersAbs.ioScheduler)
            .switchMapSingle {
                when (it) {
                    is AccessInfo.Nothing -> Single.just(ChatClientInfo.Error(IllegalStateException("no data to create chat")))
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

    private fun createChatInstance(context: Context, accessToken: String): Single<ChatClientInfo> =
        Single.create<ChatClient> { e ->
            val props = ChatClient.Properties.Builder()
                .createProperties()

            ChatClient.create(context.applicationContext,
                              accessToken,
                              props,
                              TwilioCallbackSingle<ChatClient>(e, "chatClientCreation")
            )
        }
            .map { ChatClientInfo.Data(it) as ChatClientInfo }
            .doOnSubscribe {
                chatClientInfoSubject.onNext(ChatClientInfo.Error(IllegalArgumentException("we are creating new chat client now")))
            }
}