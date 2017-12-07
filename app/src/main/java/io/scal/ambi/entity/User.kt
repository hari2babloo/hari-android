package io.scal.ambi.entity

import io.scal.ambi.R
import io.scal.ambi.extensions.binding.binders.toFrescoImagePath
import io.scal.ambi.extensions.view.IconImageUser

data class User(val uid: String = "0",
                val avatar: IconImageUser = IconImageUser(R.drawable.ic_profile.toFrescoImagePath()),
                val name: String = "John Mirror")