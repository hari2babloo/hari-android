package io.scal.ambi.model.interactor.profile

import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.user.User
import io.scal.ambi.entity.user.UserResume
import io.scal.ambi.model.repository.data.user.IUserRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import javax.inject.Inject

class ProfileResumeInteractor @Inject constructor(private val localUserDataRepository: ILocalUserDataRepository,
                                                  private val userRepository: IUserRepository) : IProfileResumeInteractor {

    override fun loadCurrentUser(): Observable<User> =
        localUserDataRepository.observeCurrentUser()

    override fun loadResumeInfo(userUid: String): Single<UserResume> {
        return userRepository.loadUserResume(userUid)
    }
}