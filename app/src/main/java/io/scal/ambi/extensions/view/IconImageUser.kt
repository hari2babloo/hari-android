package io.scal.ambi.extensions.view

import com.ambi.work.R
import io.scal.ambi.extensions.binding.binders.toFrescoImagePath

class IconImageUser(iconPath: String) : IconImage(iconPath, R.drawable.ic_profile) {

    constructor() : this(R.drawable.ic_profile.let { it.toFrescoImagePath() })
}