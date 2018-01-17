package io.scal.ambi.entity.chat

import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.trueOrThrow
import java.io.Serializable

sealed class PreviewChatItem(open val description: ChatChannelDescription,
                             open val lastMessage: ChatMessage?,
                             open val hasNewMessages: Boolean) : Serializable {

    data class Direct(override val description: ChatChannelDescription,
                      val members: List<User>,
                      override val lastMessage: ChatMessage?,
                      override val hasNewMessages: Boolean) :
        PreviewChatItem(description, lastMessage, hasNewMessages)

    data class Group(override val description: ChatChannelDescription,
                     val friendlyChannels: List<ChatChannelDescription>,
                     val members: List<User>,
                     override val lastMessage: ChatMessage?,
                     override val hasNewMessages: Boolean) :
        PreviewChatItem(description, lastMessage, hasNewMessages) {

        init {
            friendlyChannels.contains(description).trueOrThrow("current channel is not in all channel list!")
        }
    }
}