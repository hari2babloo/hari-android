package io.scal.ambi.entity.chat

import io.scal.ambi.entity.User
import io.scal.ambi.extensions.view.IconImage
import org.joda.time.DateTime

sealed class SmallChatItem(open val uid: String,
                           open val icon: IconImage,
                           open val title: String,
                           open val creationDateTime: DateTime,
                           open val lastMessage: ChatMessage?,
                           open val hasNewMessages: Boolean) {

    data class Direct(override val uid: String,
                      override val creationDateTime: DateTime,
                      val otherUser: User,
                      override val lastMessage: ChatMessage?,
                      override val hasNewMessages: Boolean) :
        SmallChatItem(uid, otherUser.avatar, otherUser.name, creationDateTime, lastMessage, hasNewMessages)

    data class Group(override val uid: String,
                     override val creationDateTime: DateTime,
                     override val icon: IconImage,
                     override val title: String,
                     val members: List<User>,
                     override val lastMessage: ChatMessage?,
                     override val hasNewMessages: Boolean) :
        SmallChatItem(uid, icon, title, creationDateTime, lastMessage, hasNewMessages)
}