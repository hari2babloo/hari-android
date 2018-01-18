package io.scal.ambi.ui.home.chat.details.data

import io.scal.ambi.entity.chat.ChatChannelDescription
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.view.IconImage

data class UIChatInfo(val channelDescription: ChatChannelDescription,
                      val friendlyChatDescriptions: List<ChatChannelDescription>?,
                      val icon: IconImage,
                      val title: CharSequence,
                      val description: CharSequence,
                      val members: List<User>,
                      val canAddUsers: Boolean?) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UIChatInfo) return false

        if (channelDescription != other.channelDescription) return false
        if (friendlyChatDescriptions != other.friendlyChatDescriptions) return false
        if (icon != other.icon) return false
        if (title.toString() != other.title.toString()) return false
        if (description.toString() != other.description.toString()) return false
        if (members != other.members) return false
        if (canAddUsers != other.canAddUsers) return false

        return true
    }

    override fun hashCode(): Int {
        var result = channelDescription.hashCode()
        result = 31 * result + (friendlyChatDescriptions?.hashCode() ?: 0)
        result = 31 * result + icon.hashCode()
        result = 31 * result + members.hashCode()
        result = 31 * result + (canAddUsers?.hashCode() ?: 0)
        return result
    }
}