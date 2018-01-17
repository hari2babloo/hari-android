package io.scal.ambi.entity.chat

import io.scal.ambi.entity.user.User

sealed class FullChatItem(open val description: ChatChannelDescription) {

    data class Direct(override val description: ChatChannelDescription,
                      val members: List<User>) :
        FullChatItem(description)

    data class Group(override val description: ChatChannelDescription,
                     val friendlyChannels: List<ChatChannelDescription>,
                     val creator: User,
                     val members: List<User>) :
        FullChatItem(description)
}