package io.scal.ambi.model.interactor.home.calendar

import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.extensions.trueOrThrow
import io.scal.ambi.model.repository.data.calendar.ICalendarRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import org.joda.time.LocalDate
import java.security.SecureRandom
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CalendarListInteractor @Inject internal constructor(private val localUserDataRepository: ILocalUserDataRepository,
                                                          private val calendarRepository: ICalendarRepository,
                                                          private val cachedCalendarEventsRepository: CachedCalendarEventsRepository,
                                                          private val rxSchedulersAbs: RxSchedulersAbs) :
    ICalendarListInteractor {

    override fun loadCurrentUser(): Observable<User> =
        localUserDataRepository.observeCurrentUser()

    override fun loadEventsForRange(startDate: LocalDate, endDate: LocalDate): Observable<CalendarEventsForDay> {
        (startDate < endDate).trueOrThrow("startDate can not be bigger then endDate")

        return Single
            .fromCallable {
                var currentDate = startDate
                val dateRange = mutableListOf<LocalDate>()
                while (currentDate <= endDate) {
                    dateRange.add(currentDate)
                    currentDate = currentDate.plusDays(1)
                }
                dateRange
            }
            .subscribeOn(rxSchedulersAbs.computationScheduler)
            .flatMapObservable { Observable.fromIterable(it) }
            .observeOn(rxSchedulersAbs.ioScheduler)
            .flatMap { loadEventForDate(it) }
    }

    private fun loadEventForDate(eventDate: LocalDate): Observable<CalendarEventsForDay> {
        return cachedCalendarEventsRepository.getCachedDataForDay(eventDate)
            .toSingle()
            .onErrorResumeNext(
                calendarRepository.loadEventForDate(eventDate)
                    .delay(SecureRandom().nextInt(10).toLong(), TimeUnit.SECONDS)
                    .subscribeOn(rxSchedulersAbs.ioScheduler)
                    .doOnSuccess { cachedCalendarEventsRepository.updateCachedDataForDay(eventDate, it) }
            )
            .toObservable()
    }
}