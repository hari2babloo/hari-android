package io.scal.ambi.ui.global.view

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

class SquareRelativeLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) :
    RelativeLayout(context, attrs, defStyleAttr, defStyleRes) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val specWidth = MeasureSpec.getSize(widthMeasureSpec)
        if (specWidth > 0) {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(specWidth, MeasureSpec.EXACTLY))
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)

            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY))
        }
    }
}