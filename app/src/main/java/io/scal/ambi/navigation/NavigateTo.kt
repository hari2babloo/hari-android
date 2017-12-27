package io.scal.ambi.navigation

import io.scal.ambi.entity.chat.ChatChannelDescription
import io.scal.ambi.extensions.trueOrThrow

object NavigateTo {

    const val LOGIN = "LOGIN"
    const val REGISTER = "REGISTER"
    const val FORGOT_PASSWORD = "FORGOT_PASSWORD"

    const val HOME = "HOME_MAIN"

    const val CHANGE_AUDIENCE = "CHANGE_AUDIENCE"

    const val CREATE_STATUS = "CREATE_STATUS"
    const val CREATE_ANNOUNCEMENT = "CREATE_ANNOUNCEMENT"
    const val CREATE_POLL = "CREATE_POLL"

    const val CHAT_DETAILS = "CHAT_DETAILS"
    const val CHAT_NEW_MESSAGE = "CHAT_NEW_MESSAGE"
    const val CHAT_CHANNEL_SELECTION = "CHAT_CHANNEL_SELECTION"

    const val PROFILE_DETAILS = "PROFILE_DETAILS"
}

class NavigateToParamChatChannelSelection(val selectedChatDescription: ChatChannelDescription,
                                          val allChatDescriptions: List<ChatChannelDescription>) {
    init {
        allChatDescriptions.contains(selectedChatDescription).trueOrThrow("all chat description should contain selected one")
    }
}