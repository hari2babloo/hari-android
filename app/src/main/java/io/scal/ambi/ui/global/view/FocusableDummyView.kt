package io.scal.ambi.ui.global.view

import android.content.Context
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import android.view.View.OnFocusChangeListener
import io.scal.ambi.ui.global.KeyboardUtils

class FocusableDummyView @JvmOverloads constructor(context: Context,
                                                   attrs: AttributeSet? = null,
                                                   defStyleAttr: Int = 0) :
    View(context.applicationContext, attrs, defStyleAttr) {

    private var focusChangeListener = OnFocusChangeListener { _, hasFocus -> if (hasFocus) KeyboardUtils.hideSoftKeyboard(context) }

    init {
        isFocusableInTouchMode = true
        isFocusable = true
        ViewCompat.setFocusedByDefault(this, true)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        onFocusChangeListener = focusChangeListener
    }

    override fun onDetachedFromWindow() {
        onFocusChangeListener = null

        super.onDetachedFromWindow()
    }
}