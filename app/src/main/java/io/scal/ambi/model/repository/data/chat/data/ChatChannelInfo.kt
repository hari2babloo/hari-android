package io.scal.ambi.model.repository.data.chat.data

import org.joda.time.DateTime

data class ChatChannelInfo(val uid: String,
                           val type: Type,
                           val name: String?,
                           val lastMessage: ChatChannelMessage?,
                           val dateTime: DateTime,
                           val hasNewMessages: Boolean,
                           val memberUids: List<String>) {

    enum class Type(val serverName: String?) {
        SIMPLE(null),
        ORG_GROUP("group"),
        ORG_CLASS("class")
    }
}