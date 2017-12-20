package io.scal.ambi.ui.home.newsfeed.list.data

import io.scal.ambi.entity.User

data class UILikes(private val currentUser: User,
                   val allUsersLiked: List<User>) {

    val currentUserLiked: Boolean
        get() = null != allUsersLiked.firstOrNull { it.uid == currentUser.uid }
}