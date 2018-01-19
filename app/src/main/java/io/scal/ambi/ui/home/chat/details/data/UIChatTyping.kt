package io.scal.ambi.ui.home.chat.details.data

import android.content.Context
import com.ambi.work.R
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.generateNamesText

data class UIChatTyping(val users: List<User>) {

    fun getTypingUsers(context: Context): String {
        return generateNamesText(context, users, R.string.chat_details_text_is_typing_this, R.string.chat_details_text_are_typing_this, null)
    }
}