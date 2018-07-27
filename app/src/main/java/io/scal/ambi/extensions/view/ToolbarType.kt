package io.scal.ambi.extensions.view

import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import com.ambi.work.R
import com.facebook.drawee.view.SimpleDraweeView

data class ToolbarType constructor(val leftIcon: IconImage?,
                                   val leftIconClickListener: Runnable?,
                                   val content: Content?,
                                   val rightIcon: IconImage?,
                                   val rightIconClickListener: Runnable?,
                                   val rightIcon2: IconImage? = null,
                                   val rightIconClickListener2: Runnable? = null,
                                   var collapsingFlags: Int? = null,
                                   var scrollFlags: Int? = null,
                                   var leftIconCustomization: IconCustomization? = null,
                                   var rightIconCustomization: IconCustomization? = null,
                                   var rightIconCustomization2: IconCustomization? = null) {

    constructor(leftIcon: IconImage?, content: Content?, rightIcon: IconImage?) :
        this(leftIcon, null, content, rightIcon, null)

    constructor(leftIcon: IconImage?, content: Content?, rightIcon: IconImage?,rightIcon2: IconImage?) :
            this(leftIcon, null, content, rightIcon, null, rightIcon2, null)

    constructor(@DrawableRes leftIcon: Int) : this(IconImage(leftIcon), null, null)

    constructor(@DrawableRes leftIcon: Int, @DrawableRes rightIcon: Int) : this(IconImage(leftIcon), null, IconImage(rightIcon))

    constructor(@DrawableRes leftIcon: Int?, content: Content?, @DrawableRes rightIcon: Int?) :
        this(leftIcon?.let { IconImage(it) }, content, rightIcon?.let { IconImage(it) })

    constructor(@DrawableRes leftIcon: Int?, content: Content?, @DrawableRes rightIcon: Int?, @DrawableRes rightIcon2: Int?) :
            this(leftIcon?.let { IconImage(it) }, content, rightIcon?.let { IconImage(it) }, rightIcon2?.let { IconImage(it) })

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