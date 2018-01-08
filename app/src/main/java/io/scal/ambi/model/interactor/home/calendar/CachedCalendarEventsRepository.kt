package io.scal.ambi.model.interactor.home.calendar

import io.reactivex.Observable
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import org.joda.time.LocalDate
import javax.inject.Inject

internal class CachedCalendarEventsRepository @Inject constructor(private val rxSchedulersAbs: RxSchedulersAbs) {

    private val events = mutableMapOf<LocalDate, CachedEvent>()

    fun updateCachedDataForDay(eventDate: LocalDate, calendarEventsForDay: CalendarEventsForDay) {

    }

    fun getCachedDataForDay(eventDate: LocalDate): Observable<CalendarEventsForDay> {
        return Observable.empty()
    }

    private data class CachedEvent(val date: LocalDate, val event: CalendarEventsForDay)
}