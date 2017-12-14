package io.scal.ambi.extensions.view

import io.scal.ambi.R
import io.scal.ambi.extensions.binding.binders.toFrescoImagePath

class IconImageUser(iconPath: String?) : IconImage(iconPath ?: R.drawable.ic_profile.toFrescoImagePath(), R.drawable.ic_profile) {

    constructor() : this(R.drawable.ic_profile.toFrescoImagePath())
}