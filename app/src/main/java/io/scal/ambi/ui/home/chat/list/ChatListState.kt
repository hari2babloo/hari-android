package io.scal.ambi.ui.home.chat.list

import io.scal.ambi.ui.home.chat.list.data.UIChatList

internal sealed class ChatListProgressState {

    object NoProgress : ChatListProgressState()

    object EmptyProgress : ChatListProgressState()

    object RefreshProgress : ChatListProgressState()

    object PageProgress : ChatListProgressState()
}

internal sealed class ChatListErrorState {

    object NoErrorState : ChatListErrorState()

    data class FatalErrorState(val error: String) : ChatListErrorState()

    data class NonFatalErrorState(val error: String) : ChatListErrorState()
}

internal sealed class ChatListDataState {

    object Empty : ChatListDataState()

    data class Data(val chats: List<UIChatList>) : ChatListDataState()
}