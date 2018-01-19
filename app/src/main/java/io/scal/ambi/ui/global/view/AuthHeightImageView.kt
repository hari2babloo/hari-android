package io.scal.ambi.ui.global.view

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet

class AuthHeightImageView : AppCompatImageView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        drawable?.run {
            if (0 != intrinsicWidth) {
                val scale = measuredWidth.toFloat() / intrinsicWidth
                val resultHeight = Math.round(scale * intrinsicHeight)
                super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(resultHeight, MeasureSpec.EXACTLY))
            }
        }
    }
}