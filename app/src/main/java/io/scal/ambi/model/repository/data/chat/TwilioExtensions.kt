package io.scal.ambi.model.repository.data.chat

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.twilio.chat.CallbackListener
import com.twilio.chat.Channel
import com.twilio.chat.ChannelDescriptor
import com.twilio.chat.Message
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.scal.ambi.R
import io.scal.ambi.extensions.binding.binders.toFrescoImagePath
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import org.joda.time.DateTime
import org.json.JSONObject
import timber.log.Timber

internal fun ChannelDescriptor.convertToChannel(): Single<Channel> =
    Single
        .create<Channel> { e ->
            val listener = object : CallbackListener<Channel>() {
                override fun onSuccess(p0: Channel) {
                    if (!e.isDisposed) {
                        e.onSuccess(p0)
                    }
                }
            }
            getChannel(listener)
        }
        .flatMap { channel -> channel.waitForSync() }

internal fun Channel.waitForSync(): Single<Channel> =
    if (synchronizationStatus == Channel.SynchronizationStatus.ALL) {
        Single.just(this)
    } else {
        Single.create { e ->
            val listener = object : SimpleChannelListener() {
                override fun onSynchronizationChanged(p0: Channel) {
                    super.onSynchronizationChanged(p0)
                    if (p0.synchronizationStatus == Channel.SynchronizationStatus.ALL) {
                        removeListener(this)
                        if (!e.isDisposed) {
                            e.onSuccess(p0)
                        }
                    }
                }
            }

            addListener(listener)

            e.setCancellable { safeRemoveListener(listener) }
        }
    }

internal fun Channel.getChatLastMessages(count: Int): Single<List<Message>> {
    return waitForSync().
        flatMap { channel ->
            Single
                .create<List<Message>> { e ->
                    val listener = object : CallbackListener<List<Message>>() {
                        override fun onSuccess(p0: List<Message>) {
                            if (!e.isDisposed) {
                                e.onSuccess(p0)
                            }
                        }
                    }
                    channel.messages.getLastMessages(count, listener)
                }
        }
}

internal fun ChannelDescriptor.getChatTypeFromTwilio(): ChatChannelInfo.Type? =
    getChatTypeFromTwilio(attributes, membersCount, friendlyName)

internal fun Channel.getChatTypeFromTwilio(): ChatChannelInfo.Type? =
    getChatTypeFromTwilio(attributes, members.membersList.size.toLong(), friendlyName)

private fun getChatTypeFromTwilio(attrs: JSONObject, membersCount: Long, friendlyName: String): ChatChannelInfo.Type? {
    val type = if (attrs.has("conversationType")) {
        when (attrs.getString("conversationType")) {
            "group" -> ChatChannelInfo.Type.GROUP
            "class" -> ChatChannelInfo.Type.CLASS
            else    -> null
        }
    } else {
        ChatChannelInfo.Type.DIRECT
    }

    return when {
        type == ChatChannelInfo.Type.DIRECT && membersCount == 2L                  -> type
        type == ChatChannelInfo.Type.CLASS && "general".equals(friendlyName, true) -> type
        type == ChatChannelInfo.Type.GROUP                                         -> type
        else                                                                       -> null
    }
}

internal fun Channel.getChatNameFromTwilio(): String? =
    if (attributes.has("purpoes")) {
        attributes.getString("purpoes")
    } else {
        null
    }

private val gsonParser = JsonParser()

internal fun Message?.toChatMessage(): ChatChannelMessage? {
    if (null == this) {
        return null
    }
    try {
        val messageStructure = gsonParser.parse(messageBody).asJsonObject
        if (!messageStructure.has("type") || !messageStructure.has("payload")) {
            throw IllegalStateException("wrong message structure! we can not find 'type' in object $messageBody")
        }
        val messageType = messageStructure.get("type").asJsonPrimitive.asString
        val messagePayload = messageStructure.get("payload").asJsonObject
        val media =
            when (messageType) {
                "IMAGE_UPLOAD" -> createChatAttachment(messagePayload, true)
                "FILE_UPLOAD"  -> createChatAttachment(messagePayload, false)
                "HANDSHAKE"    -> ChatChannelMessage.Media(R.drawable.ic_chat_handshake.toFrescoImagePath(), true, "", 0L)
                else           -> null
            }
        val message =
            when (messageType) {
                "PLAINTEXT" -> createChatMessage(messagePayload)
                else        -> ""
            }
        return ChatChannelMessage(sid, author, DateTime(timeStampAsDate.time), message, media)
    } catch (e: Exception) {
        val result =
            when (e) {
                is JsonSyntaxException   -> null
                is IllegalStateException -> null
                else                     -> throw e
            }
        Timber.w(e, "skip this message")
        return result
    }
}

private fun createChatAttachment(messagePayload: JsonObject, image: Boolean): ChatChannelMessage.Media {
    if (!messagePayload.has("url") || !messagePayload.has("file_name") || !messagePayload.has("file_size")) {
        throw IllegalStateException("wrong attachment structure in $messagePayload")
    }
    return ChatChannelMessage.Media(
        messagePayload.get("url").asString,
        image,
        messagePayload.get("file_name").asString,
        messagePayload.get("file_size").asLong
    )
}

private fun createChatMessage(messagePayload: JsonObject): String {
    if (!messagePayload.has("text")) {
        throw IllegalStateException("wrong text structure in $messagePayload")
    }
    return messagePayload.get("text").asString
}

internal fun Channel.safeRemoveListener(listener: SimpleChannelListener) {
    try {
        removeListener(listener)
    } catch (e: Exception) {
    }
}

internal fun ChannelDescriptor.convertToChannelInfo(rxSchedulersAbs: RxSchedulersAbs): Observable<ChatChannelInfo> {
    val type = getChatTypeFromTwilio()
    if (null == type) {
        Timber.w("can not convert chat descriptor to chat info")
        return Observable.empty<ChatChannelInfo>()
    }
    return convertToChannel()
        .flatMapObservable { it.convertToChannelInfo(rxSchedulersAbs, type) }
}

internal fun Channel.convertToChannelInfo(rxSchedulersAbs: RxSchedulersAbs,
                                          type: ChatChannelInfo.Type?): Observable<ChatChannelInfo> {
    val chatType = type ?: getChatTypeFromTwilio()

    if (null == chatType) {
        Timber.w("can not convert chat descriptor to chat info")
        return Observable.empty<ChatChannelInfo>()
    }

    return waitForSync()
        .flatMapObservable { channel ->
            Observable.combineLatest(
                channel.convertToUnconsumedMessagesCount().toObservable(),
                channel.convertToMessagesCount().toObservable(),
                BiFunction<Long, Long, Triple<Channel, Long, Long>> { t1, t2 -> Triple(channel, t1, t2) }
            )
        }
        .firstOrError()
        .observeOn(rxSchedulersAbs.computationScheduler)
        .flatMapObservable { tripple ->
            val channel = tripple.first
            channel.getChatLastMessages(1)
                .map { lastMessages ->
                    val lastMessage = lastMessages.lastOrNull()

                    ChatChannelInfo(
                        channel.sid,
                        chatType,
                        channel.getChatNameFromTwilio(),
                        lastMessage?.toChatMessage(),
                        DateTime(dateCreatedAsDate.time),
                        tripple.second > tripple.third,
                        channel.members.membersList.map { it.identity }
                    )
                }
                .toObservable()
        }
}

private fun Channel.convertToUnconsumedMessagesCount(): Single<Long> =
    Single
        .create<Long> { e ->
            val listener = object : CallbackListener<Long>() {
                override fun onSuccess(p0: Long) {
                    if (!e.isDisposed) {
                        e.onSuccess(p0)
                    }
                }
            }
            getUnconsumedMessagesCount(listener)
        }

private fun Channel.convertToMessagesCount(): Single<Long> =
    Single
        .create<Long> { e ->
            val listener = object : CallbackListener<Long>() {
                override fun onSuccess(p0: Long) {
                    if (!e.isDisposed) {
                        e.onSuccess(p0)
                    }
                }
            }
            getMessagesCount(listener)
        }