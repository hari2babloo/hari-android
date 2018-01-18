package io.scal.ambi.model.repository.data.chat.utils

import com.ambi.work.R
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.twilio.chat.Channel
import com.twilio.chat.ChannelDescriptor
import com.twilio.chat.ChatClient
import com.twilio.chat.Message
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.scal.ambi.entity.organization.OrganizationType
import io.scal.ambi.extensions.binding.binders.toFrescoImagePath
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.repository.data.chat.data.ChatChannelInfo
import io.scal.ambi.model.repository.data.chat.data.ChatChannelMessage
import org.joda.time.DateTime
import org.json.JSONObject
import timber.log.Timber

internal fun ChannelDescriptor.convertToChannel(): Single<Channel> =
    Single
        .create<Channel> { e -> getChannel(TwilioCallbackSingle(e, "channel")) }
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
    return waitForSync()
        .flatMap { channel ->
            Single.create<List<Message>> { e ->
                channel.messages.getLastMessages(count, TwilioCallbackSingle(e, "lastMsg"))
            }
        }
}

internal fun Channel.getChatOrganization(): ChatChannelInfo.OrganizationSmall? =
    getChatOrganization(attributes, members.membersList.size.toLong(), friendlyName)

private fun getChatOrganization(attrs: JSONObject, membersCount: Long, friendlyName: String): ChatChannelInfo.OrganizationSmall? {
    val twilioChatInfo =
        try {
            Gson().fromJson<TwilioChatInfo>(attrs.toString(), TwilioChatInfo::class.java)
        } catch (e: Exception) {
            TwilioChatInfo()
        }

    val organizationType = twilioChatInfo.conversationType.toOrganizationType()
    return when {
        null == twilioChatInfo.conversationType  ->
            null
        null != organizationType &&
            null != twilioChatInfo.organizationName &&
            null != twilioChatInfo.organizationSlug &&
            "general".equals(friendlyName, true) ->
            ChatChannelInfo.OrganizationSmall(organizationType, twilioChatInfo.organizationSlug, twilioChatInfo.organizationName)
        else                                     ->
            throw IllegalStateException("wrong chat information")
    }
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
        return ChatChannelMessage(sid, messageIndex, author, DateTime(timeStampAsDate.time), message, media)
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

internal fun ChannelDescriptor.convertToChannelInfo(rxSchedulersAbs: RxSchedulersAbs): Maybe<ChatChannelInfo> {
    return convertToChannel()
        .flatMapMaybe { it.convertToChannelInfo(rxSchedulersAbs) }
}

internal fun Channel.convertToChannelInfo(rxSchedulersAbs: RxSchedulersAbs): Maybe<ChatChannelInfo> {
    return waitForSync()
        .flatMapObservable { channel ->
            val organizationSmall = getChatOrganization()

            Observable.combineLatest(
                channel.convertToUnconsumedMessagesCount().toObservable(),
                channel.convertToMessagesCount().toObservable(),
                BiFunction<Long, Long, Array<Any?>> { t1, t2 -> arrayOf(channel, organizationSmall, t1, t2) }
            )
        }
        .firstOrError()
        .observeOn(rxSchedulersAbs.computationScheduler)
        .flatMapMaybe { array ->
            val unconsumedMessagesCount = array[2] as Long
            val messagesCount = array[3] as Long
            val channel = array[0] as Channel
            channel.getChatLastMessages(1)
                .map { lastMessages ->
                    val lastMessage = lastMessages.lastOrNull()

                    ChatChannelInfo(
                        channel.sid,
                        channel.createdBy,
                        array[1] as? ChatChannelInfo.OrganizationSmall,
                        lastMessage?.toChatMessage(),
                        DateTime(dateCreatedAsDate.time),
                        unconsumedMessagesCount > messagesCount,
                        channel.members.membersList.map { it.identity }
                    )
                }
                .toMaybe()
        }
        .onErrorResumeNext(Maybe.empty<ChatChannelInfo>())
}

private fun Channel.convertToUnconsumedMessagesCount(): Single<Long> =
    Single.create<Long> { e ->
        getUnconsumedMessagesCount(TwilioCallbackSingle(e, "unconsumedMessagesCount"))
    }

private fun Channel.convertToMessagesCount(): Single<Long> =
    Single.create<Long> { e -> getMessagesCount(TwilioCallbackSingle(e, "messagesCount")) }

internal fun OrganizationType.toServerName(): String =
    when (this) {
        OrganizationType.GROUP     -> "group"
        OrganizationType.CLASS     -> "class"
        OrganizationType.COMMUNITY -> "community"
    }

internal fun String?.toOrganizationType(): OrganizationType? =
    when (this?.toLowerCase()) {
        "group"     -> OrganizationType.GROUP
        "class"     -> OrganizationType.CLASS
        "community" -> OrganizationType.COMMUNITY
        else        -> null
    }

internal fun ChatClient.getChannelByUid(uid: String): Single<Channel> {
    return Single.create<Channel> { e -> channels.getChannel(uid, TwilioCallbackSingle(e, "channel info")) }
        .flatMap { it.waitForSync() }
}