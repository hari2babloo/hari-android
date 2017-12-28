package io.scal.ambi.model.interactor.home.calendar

import io.reactivex.Observable
import io.scal.ambi.entity.user.User

interface ICalendarListInteractor {

    fun loadCurrentUser(): Observable<User>
}