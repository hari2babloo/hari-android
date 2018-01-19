package io.scal.ambi.ui.home.chat.channel

internal sealed class ChatChannelSelectionProgressState {

    object NoProgress : ChatChannelSelectionProgressState()

    object Progress : ChatChannelSelectionProgressState()
}

internal sealed class ChatChannelSelectionErrorState {

    object NoErrorState : ChatChannelSelectionErrorState()

    data class FatalErrorState(val error: String) : ChatChannelSelectionErrorState()

    data class NonFatalErrorState(val error: String) : ChatChannelSelectionErrorState()
}

internal sealed class ChatChannelSelectionDataState {

    data class Data(val allChannelDescriptions: List<UIChatChannelDescription>) : ChatChannelSelectionDataState()
}