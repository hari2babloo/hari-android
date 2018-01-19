package io.scal.ambi.ui.home.chat.details.data

import io.scal.ambi.entity.user.User

data class UIChatLikes(private val currentUserId: String,
                       val allUsersLiked: List<User>) {

    val currentUserLiked: Boolean
        get() = null != allUsersLiked.firstOrNull { it.uid == currentUserId }
}