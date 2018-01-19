package io.scal.ambi.model.repository.data.chat.data

import com.twilio.chat.ChatClient

internal sealed class ChatClientInfo(val chatClient: ChatClient?) {

    class Data(chatClient: ChatClient) : ChatClientInfo(chatClient)

    class Error(val error: Throwable) : ChatClientInfo(null)
}