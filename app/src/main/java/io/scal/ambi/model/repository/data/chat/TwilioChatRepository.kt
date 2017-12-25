package io.scal.ambi.model.repository.data.chat

import android.content.Context
import com.twilio.chat.CallbackListener
import com.twilio.chat.ChatClient
import com.twilio.chat.ErrorInfo
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.data.server.ChatApi
import io.scal.ambi.model.repository.local.StrongRefPrefser
import javax.inject.Inject

class TwilioChatRepository @Inject constructor(context: Context,
                                               private val rxSchedulersAbs: RxSchedulersAbs,
                                               private val chatApi: ChatApi) : IChatRepository {

    private val prefs = StrongRefPrefser(context.getSharedPreferences("localTwilioData", Context.MODE_PRIVATE))

    private val accessTokenSubject = PublishSubject.create<String>()
    private val chatClientInfoSubject = BehaviorSubject.create<ChatClientInfo>()
    private val chatClientObserbable
        get() = chatClientInfoSubject.filter { it is ChatClientInfo.Data }.map { it.chatClient!! }

    init {
        accessTokenSubject
            .observeOn(rxSchedulersAbs.ioScheduler)
            .switchMap { createChatInstance(context, it) }
            .subscribe { chatClientInfoSubject.onNext(it) }
    }

//    todo make access token api call somehow


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
                                          e.onNext(ChatClientInfo.Error(IllegalStateException("can not create chat. code: ${errorInfo.code}, message: ${errorInfo.message}")))
                                      }
                                  }
                              })
        }
}