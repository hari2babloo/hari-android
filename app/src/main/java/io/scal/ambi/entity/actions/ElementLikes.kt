package io.scal.ambi.entity.actions

import io.scal.ambi.entity.User

data class ElementLikes(val currentUserLiked: Boolean,
                        val allUsersLiked: List<User>)