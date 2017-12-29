package io.scal.ambi.ui.home.calendar.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import io.scal.ambi.R
import io.scal.ambi.databinding.ItemCalendarDayWithNameBinding

class CalendarWeekMonthView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private var bindings: List<ItemCalendarDayWithNameBinding>? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.element_calendar_week_month, this, true)
    }

    internal fun setupDays(uiCalendarGroupDays: UICalendarGroupDays) {
        if (null == bindings) {
            bindings = (0 until childCount).map { getChildAt(it) }.map { ItemCalendarDayWithNameBinding.bind(it) }
        }

        uiCalendarGroupDays.days
            .forEachIndexed { index, uiCalendarDayWithEvents ->
                val itemBinding = bindings!![index]
                itemBinding.event = uiCalendarDayWithEvents
                itemBinding.weekNameVisibility = index < 7
                itemBinding.root.visibility = View.VISIBLE
                itemBinding.executePendingBindings()
            }

        (uiCalendarGroupDays.days.size until bindings!!.size)
            .forEach {
                val itemBinding = bindings!![it]
                itemBinding.root.visibility = View.GONE
            }
    }
}