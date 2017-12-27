package io.scal.ambi.ui.home.chat.details

import io.scal.ambi.entity.EmojiKeyboardState
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.binding.observable.ObservableString
import io.scal.ambi.ui.home.chat.details.data.UIChatInfo

internal sealed class ChatDetailsProgressState {

    object NoProgress : ChatDetailsProgressState()

    object EmptyInfoProgress : ChatDetailsProgressState()

    object EmptyDataProgress : ChatDetailsProgressState()

    object RefreshProgress : ChatDetailsProgressState()

    object PageProgress : ChatDetailsProgressState()
}

internal sealed class ChatDetailsErrorState {

    object NoErrorState : ChatDetailsErrorState()

    data class FatalErrorState(val error: String) : ChatDetailsErrorState()

    data class NonFatalErrorState(val error: String) : ChatDetailsErrorState()
}

sealed class ChatDetailsDataState(open val chatInfo: UIChatInfo?) {

    internal class Initial(override val chatInfo: UIChatInfo?) : ChatDetailsDataState(chatInfo) {

        override fun updateInfo(chatInfo: UIChatInfo): ChatDetailsDataState = OnlyInfo(chatInfo)
    }

    internal data class OnlyInfo(override val chatInfo: UIChatInfo) : ChatDetailsDataState(chatInfo) {

        override fun updateInfo(chatInfo: UIChatInfo): ChatDetailsDataState = copy(chatInfo = chatInfo)
        override fun moveToEmpty(): ChatDetailsDataState = Data(chatInfo, MessagesData(emptyList()))
        override fun moveToData(data: List<Any>): ChatDetailsDataState = Data(chatInfo, MessagesData(data))
    }

    internal data class Data(override val chatInfo: UIChatInfo,
                             val messages: MessagesData) : ChatDetailsDataState(chatInfo) {

        val allMessages: List<Any> = messages

        override fun updateInfo(chatInfo: UIChatInfo): ChatDetailsDataState = copy(chatInfo = chatInfo)

        override fun moveToEmpty(): ChatDetailsDataState = copy(messages = messages.updateData(emptyList()))
        override fun moveToData(data: List<Any>): ChatDetailsDataState = copy(messages = messages.updateData(data))

        override fun startTyping(user: User): ChatDetailsDataState = copy(messages = messages.startTyping(user))
        override fun stopTyping(user: User): ChatDetailsDataState = copy(messages = messages.stopTyping(user))
    }

    open fun updateInfo(chatInfo: UIChatInfo): ChatDetailsDataState = this

    open fun moveToEmpty(): ChatDetailsDataState = this
    open fun moveToData(data: List<Any>): ChatDetailsDataState = this

    open fun startTyping(user: User): ChatDetailsDataState = this
    open fun stopTyping(user: User): ChatDetailsDataState = this
}

data class MessageInputState(val userInput: ObservableString = ObservableString(),
                             val emojiKeyboardState: EmojiKeyboardState = EmojiKeyboardState.UNKNOWN) {

    fun switchKeyboard(): MessageInputState =
        when (emojiKeyboardState) {
            EmojiKeyboardState.UNKNOWN -> copy(emojiKeyboardState = EmojiKeyboardState.EMOJI)
            EmojiKeyboardState.EMOJI   -> copy(emojiKeyboardState = EmojiKeyboardState.UNKNOWN)
        }
}