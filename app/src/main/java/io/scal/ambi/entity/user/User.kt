package io.scal.ambi.entity.user

import io.scal.ambi.extensions.view.IconImageUser
import java.io.Serializable

class User private constructor(val uid: String,
                               val type: UserType,
                               val avatar: IconImageUser,
                               val email: String,
                               firstName: String,
                               lastName: String) : Serializable {

    val name: String = "$firstName $lastName".trim()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        return if (other is User) uid == other.uid else false
    }

    override fun hashCode(): Int {
        return uid.hashCode()
    }

    companion object {

        fun asStudent(uid: String,
                      avatar: IconImageUser,
                      email: String,
                      firstName: String,
                      lastName: String) = User(uid, UserType.STUDENT, avatar, email, firstName, lastName)

        // todo remove this
        fun asSimple(uid: String,
                     avatar: IconImageUser,
                     email: String,
                     firstName: String,
                     lastName: String) = User(uid, UserType.UNKNOWN, avatar, email, firstName, lastName)
    }
}