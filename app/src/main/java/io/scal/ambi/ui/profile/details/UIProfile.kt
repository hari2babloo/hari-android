package io.scal.ambi.ui.profile.details

import io.scal.ambi.extensions.view.IconImage

data class UIProfile(val name: String,
                     val avatar: IconImage,
                     val banner: IconImage?,
                     val workAt: String?,
                     val livesAt: String?,
                     val currentUser: Boolean)