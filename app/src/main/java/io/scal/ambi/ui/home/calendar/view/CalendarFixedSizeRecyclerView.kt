package io.scal.ambi.ui.home.calendar.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet

class CalendarFixedSizeRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    RecyclerView(context, attrs, defStyleAttr) {

    private var currentMode: CalendarMode? = null

    private var prevWidth: Int? = null
    private var prevHeight: Int? = null

    internal fun updateCurrentMode(mode: CalendarMode) {
        if (currentMode != mode) {
            currentMode = mode
            prevHeight = null

            requestLayout()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (null == currentMode) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        } else {
            val fullWidth = MeasureSpec.getSize(widthMeasureSpec)
            if (null != prevWidth && prevWidth == fullWidth && null != prevHeight) {
                super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(prevHeight!!, MeasureSpec.EXACTLY))
                return
            }

            val rvMargin = 0f
            val rvWidth = fullWidth - rvMargin
            val cellWidth = rvWidth / 7
            val weekNameHeight = cellWidth * 39 / 50
            val dateHeight = cellWidth * 56 / 50
            val rvHeight =
                when (currentMode) {
                    CalendarMode.WEEK  -> weekNameHeight + dateHeight
                    CalendarMode.MONTH -> weekNameHeight + dateHeight * 6
                    else               -> 0f
                }

            prevWidth = fullWidth
            prevHeight = Math.round(rvHeight)
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(prevHeight!!, MeasureSpec.EXACTLY))
        }

    }
}