package io.scal.ambi.ui.home.chat.list

import io.scal.ambi.ui.home.chat.list.data.ElementChatList
import io.scal.ambi.ui.home.chat.list.data.ElementChatListFilter

internal sealed class ChatListProgressState {

    object NoProgress : ChatListProgressState()

    object EmptyProgress : ChatListProgressState()

    object RefreshProgress : ChatListProgressState()

    object PageProgress : ChatListProgressState()
}

internal sealed class ChatListErrorState {

    object NoErrorState : ChatListErrorState()

    data class FatalErrorState(val error: Throwable) : ChatListErrorState()

    data class NonFatalErrorState(val error: Throwable) : ChatListErrorState()
}

internal sealed class ChatListDataState {

    object Empty : ChatListDataState()

    data class Data(val chats: List<ElementChatList>) : ChatListDataState()
}

internal data class ChatListFilterState(val filters: List<ElementChatListFilter>,
                                        val selectedFilter: ElementChatListFilter)
