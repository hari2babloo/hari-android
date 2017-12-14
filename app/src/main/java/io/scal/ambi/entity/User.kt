package io.scal.ambi.entity

import io.scal.ambi.extensions.view.IconImageUser

data class User(val uid: String,
                val avatar: IconImageUser,
                private val firstName: String,
                private var lastName: String) {

    val name: String = "$firstName $lastName"

}