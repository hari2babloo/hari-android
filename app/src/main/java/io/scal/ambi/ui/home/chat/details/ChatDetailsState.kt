package io.scal.ambi.ui.home.chat.details

import io.scal.ambi.entity.User
import io.scal.ambi.ui.home.chat.details.data.UIChatInfo
import io.scal.ambi.ui.home.chat.details.data.UIChatTyping

internal sealed class ChatDetailsProgressState {

    object NoProgress : ChatDetailsProgressState()

    object EmptyInfoProgress : ChatDetailsProgressState()

    object EmptyDataProgress : ChatDetailsProgressState()

    object RefreshProgress : ChatDetailsProgressState()

    object PageProgress : ChatDetailsProgressState()
}

internal sealed class ChatDetailsErrorState {

    object NoErrorState : ChatDetailsErrorState()

    data class FatalErrorState(val error: Throwable) : ChatDetailsErrorState()

    data class NonFatalErrorState(val error: Throwable) : ChatDetailsErrorState()
}

sealed class ChatDetailsDataState(open val chatInfo: UIChatInfo?) {

    class Initial(override val chatInfo: UIChatInfo?) : ChatDetailsDataState(chatInfo) {

        override fun moveToInfo(chatInfo: UIChatInfo): ChatDetailsDataState = OnlyInfo(chatInfo)
    }

    data class OnlyInfo(override val chatInfo: UIChatInfo) : ChatDetailsDataState(chatInfo) {

        override fun moveToInfo(chatInfo: UIChatInfo): ChatDetailsDataState = copy(chatInfo = chatInfo)
        override fun moveToEmpty(): ChatDetailsDataState = Data(chatInfo, emptyList())
        override fun moveToData(data: List<Any>): ChatDetailsDataState = Data(chatInfo, data)
    }

    data class Data(override val chatInfo: UIChatInfo,
                    private val messages: List<Any>,
                    private val noMoreData: Boolean = false,
                    private val chatTyping: UIChatTyping = UIChatTyping(emptyList())) : ChatDetailsDataState(chatInfo) {

        val allMessages: List<Any>

        init {
            val uiMessages = messages.toMutableList()
            if (chatTyping.users.isNotEmpty()) {
                uiMessages.add(0, chatTyping)
            }
            if (noMoreData) {
                uiMessages.add(chatInfo)
            }
            allMessages = uiMessages
        }

        override fun moveToInfo(chatInfo: UIChatInfo): ChatDetailsDataState = copy(chatInfo = chatInfo)
        override fun moveToEmpty(): ChatDetailsDataState = copy(messages = emptyList())
        override fun moveToData(data: List<Any>): ChatDetailsDataState = copy(messages = data)

        override fun startTyping(user: User): ChatDetailsDataState =
            if (null == chatTyping.users.find { it.uid == user.uid }) {
                val newTypingUsers = chatTyping.users.toMutableList()
                newTypingUsers.add(user)
                copy(chatTyping = UIChatTyping(newTypingUsers))
            } else {
                this
            }

        override fun stopTyping(user: User): ChatDetailsDataState {
            val stopTypingUser = chatTyping.users.find { it.uid == user.uid }
            return if (null == stopTypingUser) {
                this
            } else {
                val newTypingUsers = chatTyping.users.toMutableList()
                newTypingUsers.remove(stopTypingUser)
                copy(chatTyping = UIChatTyping(newTypingUsers))
            }
        }

        override fun moveToDataNoMore(): ChatDetailsDataState {
            return copy(noMoreData = true)
        }
    }

    open fun moveToInfo(chatInfo: UIChatInfo): ChatDetailsDataState = this
    open fun moveToEmpty(): ChatDetailsDataState = this
    open fun moveToData(data: List<Any>): ChatDetailsDataState = this

    open fun startTyping(user: User): ChatDetailsDataState = this
    open fun stopTyping(user: User): ChatDetailsDataState = this
    open fun moveToDataNoMore(): ChatDetailsDataState = this
}