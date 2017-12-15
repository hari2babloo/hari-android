package io.scal.ambi.entity.chat

import io.scal.ambi.entity.User
import org.joda.time.DateTime

sealed class ChatMessage(open val sender: User,
                         open val sendDate: DateTime,
                         open val message: String) {

    data class TextMessage(override val sender: User,
                           override val sendDate: DateTime,
                           override val message: String) : ChatMessage(sender, sendDate, message)

    data class AttachmentMessage(override val sender: User,
                                 override val sendDate: DateTime,
                                 override val message: String,
                                 val attachments: List<ChatAttachment>) : ChatMessage(sender, sendDate, message)
}