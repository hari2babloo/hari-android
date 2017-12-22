package io.scal.ambi.model.repository.data.chat

import android.content.Context
import com.twilio.chat.CallbackListener
import com.twilio.chat.ChatClient
import com.twilio.chat.ChatClientListener
import javax.inject.Inject
import javax.inject.Named

class TwilioChatRepository @Inject constructor(context: Context,
                                               @Named("twilioAccessToken") accessToken: String) : IChatRepository {

    init {
        val props = ChatClient.Properties.Builder()
            .createProperties()

        ChatClient.create(context.applicationContext,
                          accessToken,
                          props,
                          object : CallbackListener<ChatClient>() {
                              override fun onSuccess(client: ChatClient) {
                                  // save client for future use here
//                                  client.setListener(object : ChatClientListener {
//                                      override fun onClientSynchronization(status: ChatClient.SynchronizationStatus) {
//                                          if (status == ChatClient.SynchronizationStatus.COMPLETED) {
//                                              // Client is now ready for business, start working
//                                          }
//                                      }
//                                  })
                              }
                          })
    }
}