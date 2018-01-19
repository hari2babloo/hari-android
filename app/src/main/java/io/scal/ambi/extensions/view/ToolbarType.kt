package io.scal.ambi.extensions.view

import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import com.facebook.drawee.view.SimpleDraweeView
import com.ambi.work.R

data class ToolbarType constructor(val leftIcon: IconImage?,
                                   val leftIconClickListener: Runnable?,
                                   val content: Content?,
                                   val rightIcon: IconImage?,
                                   val rightIconClickListener: Runnable?,
                                   var collapsingFlags: Int? = null,
                                   var scrollFlags: Int? = null,
                                   var leftIconCustomization: IconCustomization? = null,
                                   var rightIconCustomization: IconCustomization? = null) {

    constructor(leftIcon: IconImage?, content: Content?, rightIcon: IconImage?) :
        this(leftIcon, null, content, rightIcon, null)

    constructor(@DrawableRes leftIcon: Int) : this(IconImage(leftIcon), null, null)

    constructor(@DrawableRes leftIcon: Int, @DrawableRes rightIcon: Int) : this(IconImage(leftIcon), null, IconImage(rightIcon))

    constructor(@DrawableRes leftIcon: Int?, content: Content?, @DrawableRes rightIcon: Int?) :
        this(leftIcon?.let { IconImage(it) }, content, rightIcon?.let { IconImage(it) })

    open class Content(@LayoutRes val layoutId: Int, val screenModel: Any?)

    class TitleContent(title: String) : Content(R.layout.item_toolbar_content_title, title)

    fun makeScrolling() {
        scrollFlags = (AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP or AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS)
    }

    fun makePin() {
        collapsingFlags = CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN
    }

    interface IconCustomization {
        fun applyNewStyle(simpleDraweeView: SimpleDraweeView)
    }
}