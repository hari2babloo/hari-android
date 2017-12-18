package io.scal.ambi.entity

import io.scal.ambi.extensions.view.IconImageUser
import java.io.Serializable

data class User(val uid: String,
                val avatar: IconImageUser,
                private val firstName: String,
                private var lastName: String) : Serializable {

    val name: String = "$firstName $lastName"

}