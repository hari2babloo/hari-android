package io.scal.ambi.extensions.view

import io.scal.ambi.extensions.binding.binders.toFrescoImagePath

class ColorIconImage(iconPath: String, val color: Int) : IconImage(iconPath) {

    constructor(icon: Int, color: Int) : this(icon.toFrescoImagePath(), color)
}