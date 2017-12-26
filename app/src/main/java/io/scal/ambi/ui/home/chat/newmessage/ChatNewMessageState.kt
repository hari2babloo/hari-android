package io.scal.ambi.ui.home.chat.newmessage

internal sealed class ChatNewMessageProgressState {

    object NoProgress : ChatNewMessageProgressState()

    object EmptyProgress : ChatNewMessageProgressState()

    object RefreshProgress : ChatNewMessageProgressState()

    object PageProgress : ChatNewMessageProgressState()
}

internal sealed class ChatNewMessageErrorState {

    object NoErrorState : ChatNewMessageErrorState()

    data class FatalErrorState(val error: String) : ChatNewMessageErrorState()

    data class NonFatalErrorState(val error: String) : ChatNewMessageErrorState()
}

internal sealed class ChatNewMessageDataState() {

    data class EmptyData(private val noData: Boolean = true) : ChatNewMessageDataState()

    data class Data(val users: List<UIUserChip>) : ChatNewMessageDataState()
}