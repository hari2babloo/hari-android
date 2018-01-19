package io.scal.ambi.model.repository.data.calendar

import io.reactivex.Single
import io.scal.ambi.entity.calendar.CalendarEvent
import io.scal.ambi.model.interactor.home.calendar.CalendarEventsForDay
import org.joda.time.LocalDate
import java.security.SecureRandom
import javax.inject.Inject

class CalendarRepository @Inject constructor() : ICalendarRepository {

    private val random = SecureRandom()

    override fun loadEventForDate(eventDate: LocalDate): Single<CalendarEventsForDay> {
        // todo add api implementation
        return Single.fromCallable {
            val events = (0 until random.nextInt(10)).map { CalendarEvent("${eventDate.toDate().time} $it", random.nextInt()) }
            CalendarEventsForDay(eventDate, events)
        }
    }
}