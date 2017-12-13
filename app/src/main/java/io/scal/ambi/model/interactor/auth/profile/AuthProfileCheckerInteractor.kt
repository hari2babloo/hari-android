package io.scal.ambi.model.interactor.auth.profile

import io.reactivex.Observable
import io.scal.ambi.entity.User
import io.scal.ambi.model.data.server.ServerResponseException
import io.scal.ambi.model.repository.data.student.IStudentRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import javax.inject.Inject

class AuthProfileCheckerInteractor @Inject constructor(private val localUserDataRepository: ILocalUserDataRepository,
                                                       private val studentRepository: IStudentRepository) : IAuthProfileCheckerInteractor {

    override fun getUserProfile(): Observable<User> {
        val currentUser = localUserDataRepository.getCurrentUser()

        return if (null == currentUser) {
            Observable.error(IllegalArgumentException("no user found"))
        } else {
            localUserDataRepository.observeCurrentUser()
                .doOnSubscribe { doUserProfileUpdate(currentUser) }
        }
    }

    private fun doUserProfileUpdate(user: User) {
        studentRepository.getStudentProfile(user.uid)
            .flatMapCompletable { localUserDataRepository.saveCurrentUser(it) }
            .subscribe(
                { },
                {
                    if (it is ServerResponseException && (it.requiresLogin || it.notFound || it.notAuthorized)) {
                        localUserDataRepository.removeAllUserInfo().subscribe()
                    }
                }
            )
    }
}