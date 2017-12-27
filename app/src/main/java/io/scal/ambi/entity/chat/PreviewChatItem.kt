package io.scal.ambi.entity.chat

import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.trueOrThrow
import io.scal.ambi.extensions.view.IconImage
import java.io.Serializable

sealed class PreviewChatItem(open val icon: IconImage,
                             open val description: ChatChannelDescription,
                             open val lastMessage: ChatMessage?,
                             open val hasNewMessages: Boolean) : Serializable {

    data class Direct(override val description: ChatChannelDescription,
                      val otherUser: User,
                      override val lastMessage: ChatMessage?,
                      override val hasNewMessages: Boolean) :
        PreviewChatItem(otherUser.avatar, description, lastMessage, hasNewMessages)

    data class Group(override val description: ChatChannelDescription,
                     val friendlyChannels: List<ChatChannelDescription>,
                     override val icon: IconImage,
                     val members: List<User>,
                     override val lastMessage: ChatMessage?,
                     override val hasNewMessages: Boolean) :
        PreviewChatItem(icon, description, lastMessage, hasNewMessages) {

        init {
            friendlyChannels.contains(description).trueOrThrow("current channel is not in all channel list!")
        }
    }
}