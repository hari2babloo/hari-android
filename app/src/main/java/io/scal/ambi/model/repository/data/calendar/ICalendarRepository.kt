package io.scal.ambi.model.repository.data.calendar

import io.reactivex.Single
import io.scal.ambi.model.interactor.home.calendar.CalendarEventsForDay
import org.joda.time.LocalDate

interface ICalendarRepository {

    fun loadEventForDate(eventDate: LocalDate): Single<CalendarEventsForDay>
}