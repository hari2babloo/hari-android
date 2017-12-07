package io.scal.ambi.entity

import io.scal.ambi.R
import io.scal.ambi.extensions.binding.toFrescoImagePath
import io.scal.ambi.extensions.view.IconImageUser

data class User(val uid: String = "0",
                val avatarIconImage: IconImageUser = IconImageUser(R.drawable.ic_profile.toFrescoImagePath()))