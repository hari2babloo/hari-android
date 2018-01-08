package io.scal.ambi.ui.home.calendar.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import io.scal.ambi.R
import io.scal.ambi.databinding.ItemCalendarDayWithNameBinding

class CalendarWeekMonthView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    private var bindings: List<ItemCalendarDayWithNameBinding>? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.element_calendar_week_month, this, true)

        orientation = VERTICAL
    }

    internal fun setupDays(uiCalendarGroupDays: UICalendarGroupDays) {
        if (null == bindings) {
            bindings = (0 until childCount)
                .map { getChildAt(it) }
                .map { it as ViewGroup }
                .flatMap { viewGroup -> (0 until viewGroup.childCount).map { viewGroup.getChildAt(it) } }
                .map { ItemCalendarDayWithNameBinding.bind(it) }
        }

        val days = uiCalendarGroupDays.days
        if (days.size % 7 != 0) {
            throw IllegalArgumentException("days should be a multiple of 7")
        }

        days
            .forEachIndexed { index, uiCalendarDayWithEvents ->
                val itemBinding = bindings!![index]
                itemBinding.event = uiCalendarDayWithEvents
                itemBinding.weekNameVisibility = index < 7
                itemBinding.executePendingBindings()
            }

        val visibleCount = days.size / 7
        (0 until visibleCount).forEach { getChildAt(it).visibility = View.VISIBLE }
        (visibleCount until childCount).forEach { getChildAt(it).visibility = View.GONE }
    }
}