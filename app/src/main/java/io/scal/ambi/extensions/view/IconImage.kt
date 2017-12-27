package io.scal.ambi.extensions.view

import io.scal.ambi.extensions.binding.binders.toFrescoImagePath
import java.io.Serializable

open class IconImage(val iconPath: String, val placeHolderIconPath: String? = null) : Serializable {

    constructor(icon: Int) : this(icon.toFrescoImagePath())

    constructor(icon: Int, placeHolderIcon: Int) : this(icon.toFrescoImagePath(), placeHolderIcon.toFrescoImagePath())

    constructor(iconPath: String, placeHolderIcon: Int) : this(iconPath, placeHolderIcon.toFrescoImagePath())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IconImage) return false

        if (iconPath != other.iconPath) return false
        if (placeHolderIconPath != other.placeHolderIconPath) return false

        return true
    }

    override fun hashCode(): Int {
        var result = iconPath.hashCode()
        result = 31 * result + (placeHolderIconPath?.hashCode() ?: 0)
        return result
    }
}