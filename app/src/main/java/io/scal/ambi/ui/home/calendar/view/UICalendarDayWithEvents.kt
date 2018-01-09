package io.scal.ambi.ui.home.calendar.view

import org.joda.time.LocalDate

data class UICalendarDayWithEvents(val date: LocalDate,
                                   val enabled: Boolean,
                                   val events: List<Event> = emptyList()) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UICalendarDayWithEvents) return false

        if (date != other.date) return false
        if (enabled != other.enabled) return false

        return true
    }

    override fun hashCode(): Int {
        var result = date.hashCode()
        result = 31 * result + enabled.hashCode()
        return result
    }

    data class Event(val color: Int)
}