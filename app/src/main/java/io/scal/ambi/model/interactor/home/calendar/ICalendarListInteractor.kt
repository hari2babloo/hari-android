package io.scal.ambi.model.interactor.home.calendar

import io.reactivex.Observable
import io.scal.ambi.entity.user.User
import org.joda.time.LocalDate

interface ICalendarListInteractor {

    fun loadCurrentUser(): Observable<User>

    fun loadEventsForRange(startDate: LocalDate, endDate: LocalDate): Observable<CalendarEventsForDay>
}