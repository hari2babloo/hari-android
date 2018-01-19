package io.scal.ambi.entity.chat

import io.scal.ambi.entity.user.User

sealed class FullChatItem(open val description: ChatChannelDescription,
                          open val members: List<User>) {

    data class Direct(override val description: ChatChannelDescription,
                      override val members: List<User>) :
        FullChatItem(description, members)

    data class Group(override val description: ChatChannelDescription,
                     val friendlyChannels: List<ChatChannelDescription>,
                     val creator: User,
                     override val members: List<User>) :
        FullChatItem(description, members)
}