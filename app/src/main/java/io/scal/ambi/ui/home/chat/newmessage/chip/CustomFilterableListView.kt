package io.scal.ambi.ui.home.chat.newmessage.chip

import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.Animation
import com.pchmn.materialchips.util.ActivityUtil
import com.pchmn.materialchips.views.FilterableListView

internal class CustomFilterableListView(context: Context) : FilterableListView(context) {

    private var hasKeyboard = false

    private val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val activity = ActivityUtil.scanForActivity(context)
        val rect = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(rect)

        val size = Point()
        activity.windowManager.defaultDisplay.getSize(size)
        val screenHeight = size.y

        val heightDifference = screenHeight - rect.bottom

        if (heightDifference > TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100f, context.resources.displayMetrics)) {
            // keyboard open
            if (!hasKeyboard) {
                visibility = View.GONE
                fadeIn()
                hasKeyboard = true
            }
        } else {
            // keyboard close
            if (hasKeyboard) {
                visibility = View.GONE
                fadeIn()
                hasKeyboard = false
            }
        }
    }

    override fun startAnimation(animation: Animation?) {}

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        rootView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
    }

    override fun onDetachedFromWindow() {
        rootView.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)

        super.onDetachedFromWindow()
    }

    override fun fadeIn() {
        if (isEnabled) {
            super.fadeIn()
        }
    }

    override fun fadeOut() {
        post {
            visibility = View.GONE
            fadeIn()
        }
    }

    fun doRealFadeOut() {
        super.fadeOut()
        visibility = View.GONE
    }

    override fun setLayoutParams(params: ViewGroup.LayoutParams) {
        (params as MarginLayoutParams).bottomMargin +=
            (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 51f, context.resources.displayMetrics)).toInt()
        super.setLayoutParams(params)
    }
}