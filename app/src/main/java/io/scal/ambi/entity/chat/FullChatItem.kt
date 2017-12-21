package io.scal.ambi.entity.chat

import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.view.IconImage
import org.joda.time.DateTime

sealed class FullChatItem(open val uid: String,
                          open val icon: IconImage,
                          open val title: String,
                          open val creationDateTime: DateTime) {

    data class Direct(override val uid: String,
                      override val creationDateTime: DateTime,
                      val otherUser: User) :
        FullChatItem(uid, otherUser.avatar, otherUser.name, creationDateTime)

    data class Group(override val uid: String,
                     val creator: User,
                     override val creationDateTime: DateTime,
                     override val icon: IconImage,
                     override val title: String,
                     val members: List<User>) :
        FullChatItem(uid, icon, title, creationDateTime)
}