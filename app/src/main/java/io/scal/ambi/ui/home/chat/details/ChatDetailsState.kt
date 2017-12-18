package io.scal.ambi.ui.home.chat.details

import io.scal.ambi.ui.home.chat.details.data.UIChatInfo
import io.scal.ambi.ui.home.chat.details.data.UIChatMessage

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
        override fun moveToEmpty(): ChatDetailsDataState = EmptyData(chatInfo)
        override fun moveToData(data: List<UIChatMessage>): ChatDetailsDataState = Data(chatInfo, data)
    }

    data class EmptyData(override val chatInfo: UIChatInfo) : ChatDetailsDataState(chatInfo) {

        override fun moveToInfo(chatInfo: UIChatInfo): ChatDetailsDataState = copy(chatInfo = chatInfo)
        override fun moveToData(data: List<UIChatMessage>): ChatDetailsDataState = Data(chatInfo, data)
    }

    data class Data(override val chatInfo: UIChatInfo,
                    val messages: List<UIChatMessage> = emptyList()) : ChatDetailsDataState(chatInfo) {

        override fun moveToInfo(chatInfo: UIChatInfo): ChatDetailsDataState = copy(chatInfo = chatInfo)
        override fun moveToEmpty(): ChatDetailsDataState = EmptyData(chatInfo)
        override fun moveToData(data: List<UIChatMessage>): ChatDetailsDataState = Data(chatInfo, data)
    }

    open fun moveToInfo(chatInfo: UIChatInfo): ChatDetailsDataState = this
    open fun moveToEmpty(): ChatDetailsDataState = this
    open fun moveToData(data: List<UIChatMessage>): ChatDetailsDataState = this
}