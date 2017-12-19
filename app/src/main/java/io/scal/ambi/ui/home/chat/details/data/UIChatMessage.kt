package io.scal.ambi.ui.home.chat.details.data

import io.scal.ambi.entity.User
import io.scal.ambi.entity.chat.ChatAttachment
import io.scal.ambi.extensions.view.IconImage
import org.joda.time.DateTime

sealed class UIChatMessage(open val sender: User,
                           open val myMessage: Boolean,
                           open val messageDateTime: DateTime,
                           open val likes: UIChatLikes) {

    data class TextMessage(override val sender: User,
                           override val myMessage: Boolean,
                           val message: String,
                           override val messageDateTime: DateTime,
                           override val likes: UIChatLikes) : UIChatMessage(sender, myMessage, messageDateTime, likes)

    data class ImageMessage(override val sender: User,
                            override val myMessage: Boolean,
                            val message: String,
                            val image: IconImage,
                            override val messageDateTime: DateTime,
                            override val likes: UIChatLikes) : UIChatMessage(sender, myMessage, messageDateTime, likes)

    data class AttachmentMessage(override val sender: User,
                                 override val myMessage: Boolean,
                                 val attachment: ChatAttachment.File,
                                 val message: String,
                                 val description: String,
                                 override val messageDateTime: DateTime,
                                 override val likes: UIChatLikes) : UIChatMessage(sender, myMessage, messageDateTime, likes)
}