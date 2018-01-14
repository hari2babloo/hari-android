package io.scal.ambi.model.repository.data.chat

import io.scal.ambi.extensions.trueOrThrow
import org.joda.time.DateTime

data class ChatChannelInfo(val uid: String,
                           val type: Type,
                           val name: String?,
                           val lastMessage: ChatChannelMessage?,
                           val dateTime: DateTime,
                           val hasNewMessages: Boolean,
                           val memberUids: List<String>) {

    init {
        if (type == Type.DIRECT) {
            (memberUids.size == 2).trueOrThrow("memberUids should be 2 in direct chats")
        }
    }

    enum class Type {
        DIRECT,
        GROUP,
        CLASS
    }
}