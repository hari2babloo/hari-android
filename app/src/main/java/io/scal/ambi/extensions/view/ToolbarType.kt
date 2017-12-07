package io.scal.ambi.extensions.view

import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import io.scal.ambi.R

data class ToolbarType constructor(val leftIcon: IconImage?,
                                   val content: Content?,
                                   val rightIcon: IconImage?) {

    constructor(@DrawableRes leftIcon: Int) : this(IconImage(leftIcon), null, null)

    constructor(@DrawableRes leftIcon: Int, @DrawableRes rightIcon: Int) : this(IconImage(leftIcon), null, IconImage(rightIcon))

    constructor(@DrawableRes leftIcon: Int?, content: Content?, @DrawableRes rightIcon: Int?) :
        this(leftIcon?.let { IconImage(it) }, content, rightIcon?.let { IconImage(it) })

    open class Content(@LayoutRes val layoutId: Int, val screenModel: Any?)

    class TitleContent(title: String) : Content(R.layout.item_toolbar_content_title, title)
}