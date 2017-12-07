package io.scal.ambi.extensions.binding.binders

import android.databinding.BindingAdapter
import android.os.Build
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Spinner
import io.scal.ambi.extensions.DrawablesUtils

/**
 * Binder for databinding
 */
object ViewDataBinder {

    @JvmStatic
    @BindingAdapter("rippleDrawable")
    fun setRippleDrawable(view: View, color: Int) {
        if (Build.VERSION_CODES.JELLY_BEAN <= Build.VERSION.SDK_INT) {
            view.background = DrawablesUtils.getSelectableDrawableFor(color)
        }
    }

    @JvmStatic
    @BindingAdapter("spinnerEnable")
    fun setSpinnerEnable(spinner: Spinner, enable: Boolean?) {
        if (null != enable) {
            spinner.isEnabled = enable
            spinner.isClickable = enable
        }
    }

    @JvmStatic
    @BindingAdapter("android:layout_marginStart")
    fun setStartMargin(view: View, startMargin: Float) {
        val layoutParams = view.layoutParams as MarginLayoutParams
        layoutParams.setMargins(startMargin.toInt(), layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin)
        view.layoutParams = layoutParams
    }

    @JvmStatic
    @BindingAdapter("android:layout_marginTop")
    fun setTopMargin(view: View, topMargin: Float) {
        val layoutParams = view.layoutParams as MarginLayoutParams
        layoutParams.setMargins(layoutParams.leftMargin, topMargin.toInt(), layoutParams.rightMargin, layoutParams.bottomMargin)
        view.layoutParams = layoutParams
    }

    @JvmStatic
    @BindingAdapter("android:layout_marginEnd")
    fun setEndMargin(view: View, endMargin: Float) {
        val layoutParams = view.layoutParams as MarginLayoutParams
        layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, endMargin.toInt(), layoutParams.bottomMargin)
        view.layoutParams = layoutParams
    }

    @JvmStatic
    @BindingAdapter("android:layout_marginBottom")
    fun setBottomMargin(view: View, bottomMargin: Float) {
        val layoutParams = view.layoutParams as MarginLayoutParams
        layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, bottomMargin.toInt())
        view.layoutParams = layoutParams
    }

    @JvmStatic
    @BindingAdapter("visibility")
    fun setVisibility(view: View, visibility: Boolean?) {
        if (null != visibility && visibility) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }
}