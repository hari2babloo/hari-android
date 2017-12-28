package io.scal.ambi.model.interactor.home.calendar

import io.reactivex.Observable
import io.scal.ambi.entity.user.User
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import javax.inject.Inject

class CalendarListInteractor @Inject constructor(private val localUserDataRepository: ILocalUserDataRepository) : ICalendarListInteractor {

    override fun loadCurrentUser(): Observable<User> =
        localUserDataRepository.observeCurrentUser()
}