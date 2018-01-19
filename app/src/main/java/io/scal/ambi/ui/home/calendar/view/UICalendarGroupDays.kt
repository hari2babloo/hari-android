package io.scal.ambi.ui.home.calendar.view

import org.joda.time.YearMonth

internal sealed class UICalendarGroupDays(open val yearMonth: YearMonth,
                                          open val days: List<UICalendarDay>) {

    data class Week(override val yearMonth: YearMonth,
                    override val days: List<UICalendarDay>) : UICalendarGroupDays(yearMonth, days)
    
    data class Month(override val yearMonth: YearMonth,
                     override val days: List<UICalendarDay>) : UICalendarGroupDays(yearMonth, days)
}