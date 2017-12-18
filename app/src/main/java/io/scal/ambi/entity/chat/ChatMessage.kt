package io.scal.ambi.entity.chat

import io.scal.ambi.entity.User
import org.joda.time.DateTime
import java.io.Serializable

sealed class ChatMessage(open val sender: User,
                         open val sendDate: DateTime,
                         open val message: String,
                         open val likes: List<User>) : Serializable {

    data class TextMessage(override val sender: User,
                           override val sendDate: DateTime,
                           override val message: String,
                           override val likes: List<User>) : ChatMessage(sender, sendDate, message, likes)

    data class AttachmentMessage(override val sender: User,
                                 override val sendDate: DateTime,
                                 override val message: String,
                                 val attachments: List<ChatAttachment>,
                                 override val likes: List<User>) : ChatMessage(sender, sendDate, message, likes) {
        init {
            if (attachments.isEmpty()) {
                throw IllegalArgumentException("attachments can not be empty")
            }
        }
    }
}