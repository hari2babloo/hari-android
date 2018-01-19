package io.scal.ambi.model.interactor.auth.profile

import io.reactivex.Observable
import io.scal.ambi.entity.user.User

interface IAuthProfileCheckerInteractor {

    fun getUserProfile(): Observable<User>
}