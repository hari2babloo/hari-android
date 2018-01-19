package io.scal.ambi.model.repository.data.chat.utils

import com.twilio.chat.Channel
import com.twilio.chat.ChatClient
import com.twilio.chat.Message
import com.twilio.chat.Messages
import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.model.repository.data.chat.data.ChatChannelMessage
import org.json.JSONObject

internal class TwilioChannelMessagesPaginator(private val chatClientObservable: Observable<ChatClient>) {

    private val messageCount = 15

    fun loadNextPage(channelSid: String, lastMessageIndex: Long?): Single<List<ChatChannelMessage>> {
        return loadPage(chatClientObservable, channelSid, lastMessageIndex)
            .flatMap { Observable.fromIterable(it).filter { it.messageIndex != lastMessageIndex }.toList() }
            .map { it.mapNotNull { it.toChatMessage() } }
    }

    fun sendChatMessage(chatUid: String, message: String): Single<ChatChannelMessage> {
        return getChatChannelMessages(chatClientObservable, chatUid)
            .flatMap {
                Single.create<Message> { e ->
                    val messageOptions = Message.options()
                    val body = generateMessageBody(message)
                    messageOptions.withBody(body)
                    it.sendMessage(messageOptions, TwilioCallbackSingle(e, "sendMessage"))
                }
            }
            .map { it.toChatMessage() }
    }

    private fun loadPage(chatClientObservable: Observable<ChatClient>, channelSid: String, lastMessageIndex: Long?): Single<List<Message>> {
        return getChatChannelMessages(chatClientObservable, channelSid)
            .flatMap {
                if (null == lastMessageIndex) {
                    Single.create<List<Message>> { e ->
                        it.getLastMessages(messageCount, TwilioCallbackSingle(e, "lastMessages"))
                    }
                        .map { it.reversed() }
                } else {
                    Single.create<List<Message>> { e ->
                        it.getMessagesBefore(lastMessageIndex, messageCount, TwilioCallbackSingle(e, "messagesBefore"))
                    }
                }
            }
    }

    private fun getChatChannelMessages(chatClientObservable: Observable<ChatClient>, channelSid: String): Single<Messages> {
        return chatClientObservable
            .firstOrError()
            .flatMap { chatClient ->
                Single.create<Channel> { e ->
                    chatClient.channels.getChannel(channelSid, TwilioCallbackSingle(e, "get channel info"))
                }
            }
            .flatMap { it.waitForSync() }
            .map { it.messages }
    }

    private fun generateMessageBody(message: String): String {
        val jsonObject = JSONObject()
        jsonObject.put("type", "PLAINTEXT")

        val payload = JSONObject()
        payload.put("text", message)
        jsonObject.put("payload", payload)

        return jsonObject.toString()
    }
}