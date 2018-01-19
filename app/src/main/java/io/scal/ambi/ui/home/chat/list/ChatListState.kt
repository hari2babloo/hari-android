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

    class Data(_chats: List<UIChatList>) : ChatListDataState() {

        val chats: List<UIChatList>

        init {
            chats = _chats.sortedByDescending { uiChatList -> uiChatList.lastMessageDateTime }
        }
    }
}