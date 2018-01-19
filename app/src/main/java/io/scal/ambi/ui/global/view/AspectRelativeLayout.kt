package io.scal.ambi.ui.global.view

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.ambi.work.R

class AspectRelativeLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    RelativeLayout(context, attrs, defStyleAttr) {

    private val aspectWidth: Int
    private val aspectHeight: Int

    init {
        if (null != attrs) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AspectRelativeLayout)

            aspectWidth = typedArray.getInteger(R.styleable.AspectRelativeLayout_aspect_width, 1)
            aspectHeight = typedArray.getInteger(R.styleable.AspectRelativeLayout_aspect_height, 1)

            typedArray.recycle()
        } else {
            aspectWidth = 1
            aspectHeight = 1
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val specWidth = MeasureSpec.getSize(widthMeasureSpec)
        if (specWidth > 0) {
            val resultHeight = Math.round(specWidth.toFloat() * aspectHeight / aspectWidth)
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(resultHeight, MeasureSpec.EXACTLY))
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)

            val resultHeight = Math.round(measuredWidth.toFloat() * aspectHeight / aspectWidth)
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(resultHeight, MeasureSpec.EXACTLY))
        }
    }
}