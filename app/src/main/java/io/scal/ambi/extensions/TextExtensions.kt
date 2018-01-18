package io.scal.ambi.extensions

import android.content.Context
import com.ambi.work.R
import io.scal.ambi.entity.user.User

fun generateNamesText(context: Context, users: List<User>, singleString: Int, multiplyString: Int, currentUser: User? = null): String {
    val namesText = StringBuilder(40)
    if (1 == users.size) {
        return context.getString(singleString, users[0].name)
    } else {
        users
            .map { if (it == currentUser) context.getString(R.string.text_you) else it.name }
            .mapIndexed { index, name ->
                when (index) {
                    users.size - 1 -> " ${context.getString(R.string.text_and)} $name"
                    0              -> name
                    else           -> ", $name"
                }
            }
            .forEach { namesText.append(it) }

        return context.getString(multiplyString, namesText.toString().trim())
    }
}