package io.scal.ambi.model.interactor.profile

import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.user.User
import io.scal.ambi.entity.user.UserResume

interface IProfileResumeInteractor {

    fun loadCurrentUser(): Observable<User>

    fun loadResumeInfo(userUid: String): Single<UserResume>
}