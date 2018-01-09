package io.scal.ambi.model.interactor.home.calendar

import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.LocalDate
import org.joda.time.Minutes
import java.util.concurrent.Executors
import javax.inject.Inject

internal class CachedCalendarEventsRepository @Inject constructor() {

    private val scheduler = Schedulers.from(Executors.newSingleThreadExecutor())
    private val events = mutableMapOf<LocalDate, CachedEvent>()

    fun updateCachedDataForDay(eventDate: LocalDate, calendarEventsForDay: CalendarEventsForDay) {
        Single.just(Pair(eventDate, calendarEventsForDay))
            .observeOn(scheduler)
            .subscribe(Consumer { events.put(it.first, CachedEvent(DateTime.now(), it.second)) })
    }

    fun getCachedDataForDay(eventDate: LocalDate): Maybe<CalendarEventsForDay> {
        return Single.just(eventDate)
            .observeOn(scheduler)
            .flatMapMaybe {
                val event = getUpToDateEventForDate(eventDate)
                if (null == event) {
                    Maybe.empty()
                } else {
                    Maybe.just(event)
                }
            }
    }

    private fun getUpToDateEventForDate(eventDate: LocalDate): CalendarEventsForDay? {
        val event = events[eventDate] ?: return null

        val duration = Duration(event.creationTime, DateTime.now())
        return if (Math.abs(duration.standardSeconds) < maxCacheLifeSeconds) {
            event.event
        } else {
            events.remove(eventDate)
            null
        }
    }

    private data class CachedEvent(val creationTime: DateTime, val event: CalendarEventsForDay)

    companion object {

        private val maxCacheLifeSeconds = Minutes.minutes(10).toStandardSeconds().seconds
    }
}