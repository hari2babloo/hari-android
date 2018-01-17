package io.scal.ambi.ui.home.chat.details

import io.scal.ambi.entity.user.User
import io.scal.ambi.ui.home.chat.details.data.UIChatInfo
import io.scal.ambi.ui.home.chat.details.data.UIChatMessage
import io.scal.ambi.ui.home.chat.details.data.UIChatTyping

internal data class MessagesData(private val data: List<Any>,
                                 private val chatInfo: UIChatInfo? = null,
                                 private val chatTyping: UIChatTyping = UIChatTyping(emptyList())) : AbstractList<Any>() {

    private val hasChatTyping: Boolean
        get() = chatTyping.users.isNotEmpty()
    private val hasChatInfo: Boolean
        get() = null != chatInfo

    fun getLastMessage(): UIChatMessage? {
        return data.findLast { it is UIChatMessage } as? UIChatMessage
    }

    override val size: Int
        get() {
            var count = data.size
            if (hasChatTyping) {
                count++
            }
            if (hasChatInfo) {
                count++
            }
            return count
        }

    override fun get(index: Int): Any {
        var normalIndex = index
        if (hasChatTyping) {
            if (0 == normalIndex) {
                return chatTyping
            }
            normalIndex--
        }
        if (data.size > normalIndex) {
            return data[normalIndex]
        }
        normalIndex -= data.size
        if (0 == normalIndex && hasChatInfo) {
            return chatInfo!!
        }

        throw IllegalStateException("shouldn't happen")
    }

    fun updateData(list: List<Any>): MessagesData = copy(data = list)

    fun startTyping(user: User): MessagesData =
        if (null == chatTyping.users.find { it.uid == user.uid }) {
            copy(chatTyping = UIChatTyping(chatTyping.users.plus(user)))
        } else {
            this
        }

    fun stopTyping(user: User): MessagesData {
        val stopTypingUser = chatTyping.users.find { it.uid == user.uid }
        return if (null == stopTypingUser) {
            this
        } else {
            copy(chatTyping = UIChatTyping(chatTyping.users.minus(stopTypingUser)))
        }
    }
}