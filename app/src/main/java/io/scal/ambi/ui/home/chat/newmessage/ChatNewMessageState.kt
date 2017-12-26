package io.scal.ambi.ui.home.chat.newmessage

import android.databinding.ObservableList
import io.scal.ambi.extensions.binding.observable.OptimizedObservableArrayList

internal sealed class ChatNewMessageProgressState {

    object NoProgress : ChatNewMessageProgressState()

    object EmptyProgress : ChatNewMessageProgressState()

    object RefreshProgress : ChatNewMessageProgressState()

    object PageProgress : ChatNewMessageProgressState()

    object TotalProgress : ChatNewMessageProgressState()
}

internal sealed class ChatNewMessageErrorState {

    object NoErrorState : ChatNewMessageErrorState()

    data class FatalErrorState(val error: String) : ChatNewMessageErrorState()

    data class NonFatalErrorState(val error: String) : ChatNewMessageErrorState()
}

internal sealed class ChatNewMessageDataState {

    data class EmptyData(private val noData: Boolean = true) : ChatNewMessageDataState()

    data class Data(val users: List<UIUserChip>, val selectedUsers: ObservableList<UIUserChip> = OptimizedObservableArrayList()) :
        ChatNewMessageDataState()
}