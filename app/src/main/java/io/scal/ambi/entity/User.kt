package io.scal.ambi.entity

import io.scal.ambi.extensions.view.IconImageUser

data class User(val uid: String,
                val avatar: IconImageUser,
                val firstName: String,
                var lastName: String) {

    val name: String = "$firstName $lastName"

    override fun equals(other: Any?): Boolean {
        if (other !is User) {
            return false
        }

        return uid == other.uid
    }

    override fun hashCode(): Int {
        return uid.hashCode()
    }
}