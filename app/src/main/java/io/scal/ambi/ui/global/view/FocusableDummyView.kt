package io.scal.ambi.ui.global.view

import android.content.Context
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import android.view.View.OnFocusChangeListener
import io.scal.ambi.ui.global.KeyboardUtils

class FocusableDummyView : View {

    private var focusChangeListener = OnFocusChangeListener { _, hasFocus -> if (hasFocus) KeyboardUtils.hideSoftKeyboard(context) }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

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