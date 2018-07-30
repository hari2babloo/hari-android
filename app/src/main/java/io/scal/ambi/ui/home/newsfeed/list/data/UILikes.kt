package io.scal.ambi.ui.home.newsfeed.list.data

import io.scal.ambi.entity.user.User

data class UILikes(private val currentUser: User,
                   private val _allUsersLiked: List<User>) {

    val allUsersLiked: List<User>

    init {
        allUsersLiked = _allUsersLiked.toMutableList()
        allUsersLiked.shuffle()
    }

    val currentUserLiked: Boolean
        get() = null != allUsersLiked.firstOrNull { it.uid == currentUser.uid }

}