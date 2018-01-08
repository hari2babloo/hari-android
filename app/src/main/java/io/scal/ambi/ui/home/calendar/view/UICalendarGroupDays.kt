package io.scal.ambi.ui.home.calendar.view

import org.joda.time.YearMonth

internal sealed class UICalendarGroupDays(open val yearMonth: YearMonth,
                                          open val days: List<UICalendarDayWithEvents>) {

    data class Week(override val yearMonth: YearMonth,
                    override val days: List<UICalendarDayWithEvents>) : UICalendarGroupDays(yearMonth, days) {

        override fun updateDays(newDays: List<UICalendarDayWithEvents>): UICalendarGroupDays = copy(days = newDays)
    }

    data class Month(override val yearMonth: YearMonth,
                     override val days: List<UICalendarDayWithEvents>) : UICalendarGroupDays(yearMonth, days) {

        override fun updateDays(newDays: List<UICalendarDayWithEvents>): UICalendarGroupDays = copy(days = newDays)
    }

    abstract fun updateDays(newDays: List<UICalendarDayWithEvents>): UICalendarGroupDays
}