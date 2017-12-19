package io.scal.ambi.ui.home.chat.details.data

import android.content.Context
import io.scal.ambi.R
import io.scal.ambi.entity.User
import io.scal.ambi.extensions.generateNamesText

data class UIChatTyping(val users: List<User>) {

    fun getTypingUsers(context: Context): String {
        return generateNamesText(context, users, R.string.text_is_typing_this, R.string.text_are_typing_this, null)
    }
}