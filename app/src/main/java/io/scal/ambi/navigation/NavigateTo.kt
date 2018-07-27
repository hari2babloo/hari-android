package io.scal.ambi.navigation

import io.scal.ambi.entity.chat.ChatChannelDescription
import io.scal.ambi.entity.user.User
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
    const val CHAT_APPEND_USERS = "CHAT_APPEND_USERS"
    const val CHAT_CHANNEL_SELECTION = "CHAT_CHANNEL_SELECTION"

    const val PROFILE_DETAILS = "PROFILE_DETAILS"
    const val PROFILE_PASSWORD_CHANGE = "PROFILE_PASSWORD_CHANGE"
    const val PROFILE_RESUME = "PROFILE_RESUME"
    const val CHAT = "CHAT";
    const val NOTIFICATIONS = "NOTIFICATIONS";
}

class NavigateToParamChatChannelSelection(val selectedChatDescription: ChatChannelDescription,
                                          val allChatDescriptions: List<ChatChannelDescription>) {
    init {
        allChatDescriptions.contains(selectedChatDescription).trueOrThrow("all chat description should contain selected one")
    }
}

class NavigateToParamChatAppendUsers(val chatDescription: ChatChannelDescription,
                                     val currentMemebers: List<User>)