package io.scal.ambi.ui.global.view

import android.content.Context
import android.support.v7.widget.AppCompatEditText
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View

class FocusClearEditText : AppCompatEditText {

    private val backButtonListener =
        View.OnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                clearFocus()
                true
            } else {
                false
            }
        }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        isFocusableInTouchMode = true
        isFocusable = true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        setOnKeyListener(backButtonListener)
    }

    override fun onDetachedFromWindow() {
        setOnKeyListener(null)

        super.onDetachedFromWindow()
    }
}