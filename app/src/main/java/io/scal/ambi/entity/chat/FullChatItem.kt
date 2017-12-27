package io.scal.ambi.entity.chat

import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.view.IconImage

sealed class FullChatItem(open val uid: String,
                          open val icon: IconImage,
                          open val description: ChatChannelDescription) {

    data class Direct(override val uid: String,
                      override val description: ChatChannelDescription,
                      val otherUser: User) :
        FullChatItem(uid, otherUser.avatar, description)

    data class Group(override val uid: String,
                     val creator: User,
                     override val description: ChatChannelDescription,
                     val friendlyChannels: List<ChatChannelDescription>,
                     override val icon: IconImage,
                     val members: List<User>) :
        FullChatItem(uid, icon, description)
}