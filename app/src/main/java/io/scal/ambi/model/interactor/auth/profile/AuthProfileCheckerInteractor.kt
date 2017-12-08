package io.scal.ambi.model.interactor.auth.profile

import io.reactivex.Observable
import io.scal.ambi.entity.User
import io.scal.ambi.model.data.server.ServerResponseException
import io.scal.ambi.model.repository.auth.AuthResult
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import io.scal.ambi.model.repository.data.student.IStudentRepository
import javax.inject.Inject

class AuthProfileCheckerInteractor @Inject constructor(private val localUserDataRepository: ILocalUserDataRepository,
                                                       private val studentRepository: IStudentRepository) : IAuthProfileCheckerInteractor {

    override fun getUserProfile(): Observable<User> {
        val cachedUserInfo = Observable
            .fromCallable {
                localUserDataRepository.getUserInfo()
                    ?.user
                    ?: throw IllegalStateException("no user found")
            }

        return Observable
            .concat(cachedUserInfo,
                    localUserDataRepository.observeUserInfo()
                        .map { it.user }
            )
            .doOnSubscribe { localUserDataRepository.getUserInfo()?.run { doUserProfileUpdate(token) } }
    }

    private fun doUserProfileUpdate(token: String) {
        studentRepository.getCurrentStudentProfile(token)
            .flatMapCompletable { localUserDataRepository.saveUserInfo(AuthResult(token, it)) }
            .subscribe(
                { },
                {
                    if (it is ServerResponseException && it.requiresLogin) {
                        localUserDataRepository.removeUserInfo()
                    }
                }
            )
    }
}