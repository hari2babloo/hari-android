package io.scal.ambi.ui.home.calendar.view

import org.joda.time.LocalDate

data class UICalendarDayWithEvents(val date: LocalDate,
                                   val enabled: Boolean,
                                   val events: List<Event> = emptyList()) {

    data class Event(val color: Int)
}