package io.scal.ambi.ui.global.view

import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import io.scal.ambi.R

class ToolbarType constructor(@DrawableRes val leftIcon: Int?,
                              val content: Content?,
                              @DrawableRes val rightIcon: Int?) {

    constructor(@DrawableRes leftIcon: Int) : this(leftIcon, null, null)

    constructor(@DrawableRes leftIcon: Int, @DrawableRes rightIcon: Int) : this(leftIcon, null, rightIcon)

    open class Content(@LayoutRes val layoutId: Int, val screenModel: Any?)

    class TitleContent(title: String) : Content(R.layout.item_toolbar_content_title, title)
}