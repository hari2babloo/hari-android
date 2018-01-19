package io.scal.ambi.model.interactor.home.calendar

import io.scal.ambi.entity.calendar.CalendarEvent
import org.joda.time.LocalDate

data class CalendarEventsForDay(val day: LocalDate,
                                val events: List<CalendarEvent>)