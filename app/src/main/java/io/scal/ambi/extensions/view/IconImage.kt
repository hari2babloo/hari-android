package io.scal.ambi.extensions.view

import io.scal.ambi.extensions.binding.binders.toFrescoImagePath
import java.io.Serializable

open class IconImage(val iconPath: String, val placeHolderIconPath: String? = null) : Serializable {

    constructor(icon: Int) : this(icon.toFrescoImagePath())

    constructor(icon: Int, placeHolderIcon: Int) : this(icon.toFrescoImagePath(), placeHolderIcon.toFrescoImagePath())

    constructor(iconPath: String, placeHolderIcon: Int) : this(iconPath, placeHolderIcon.toFrescoImagePath())
}