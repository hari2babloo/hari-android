package io.scal.ambi.ui.home.chat.details.data

import io.scal.ambi.entity.chat.ChatAttachment
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.view.IconImage
import org.joda.time.DateTime

sealed class UIChatMessage(open val uid: String,
                           open val index: Long?,
                           open val sender: User,
                           open val state: UIChatMessageStatus,
                           open val messageDateTime: DateTime,
                           open val likes: UIChatLikes) {

    data class TextMessage(override val uid: String,
                           override val index: Long?,
                           override val sender: User,
                           override val state: UIChatMessageStatus,
                           val message: String,
                           override val messageDateTime: DateTime,
                           override val likes: UIChatLikes) : UIChatMessage(uid, index, sender, state, messageDateTime, likes)

    data class ImageMessage(override val uid: String,
                            override val index: Long?,
                            override val sender: User,
                            override val state: UIChatMessageStatus,
                            val message: String,
                            val image: IconImage,
                            override val messageDateTime: DateTime,
                            override val likes: UIChatLikes) : UIChatMessage(uid, index, sender, state, messageDateTime, likes)

    data class AttachmentMessage(override val uid: String,
                                 override val index: Long?,
                                 override val sender: User,
                                 override val state: UIChatMessageStatus,
                                 val attachment: ChatAttachment.File,
                                 val message: String,
                                 val description: String,
                                 override val messageDateTime: DateTime,
                                 override val likes: UIChatLikes) : UIChatMessage(uid, index, sender, state, messageDateTime, likes)
}