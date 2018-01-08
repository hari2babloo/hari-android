package io.scal.ambi.model.interactor.home.calendar

import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.trueOrThrow
import io.scal.ambi.model.repository.data.calendar.ICalendarRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import org.joda.time.LocalDate
import javax.inject.Inject

class CalendarListInteractor @Inject internal constructor(private val localUserDataRepository: ILocalUserDataRepository,
                                                          private val calendarRepository: ICalendarRepository,
                                                          private val cachedCalendarEventsRepository: CachedCalendarEventsRepository) :
    ICalendarListInteractor {

    override fun loadCurrentUser(): Observable<User> =
        localUserDataRepository.observeCurrentUser()

    override fun loadEventsForRange(startDate: LocalDate, endDate: LocalDate): Observable<CalendarEventsForDay> {
        (startDate < endDate).trueOrThrow("startDate can not be bigger then endDate")

        return Single
            .fromCallable {
                var currentDate = startDate
                val dateRange = mutableListOf<LocalDate>()
                while (currentDate < endDate) {
                    dateRange.add(currentDate)
                    currentDate = currentDate.plusDays(1)
                }
                dateRange
            }
            .flatMapObservable { Observable.fromIterable(it) }
            .flatMap { loadEventForDate(it) }
    }

    private fun loadEventForDate(eventDate: LocalDate): Observable<CalendarEventsForDay> {
        return calendarRepository.loadEventForDate(eventDate)
            .doOnSuccess { cachedCalendarEventsRepository.updateCachedDataForDay(eventDate, it) }
            .toObservable()
            .startWith(cachedCalendarEventsRepository.getCachedDataForDay(eventDate))
    }
}