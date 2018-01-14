package io.scal.ambi.model.interactor.auth.profile

import android.os.SystemClock
import io.reactivex.Observable
import io.scal.ambi.entity.user.User
import io.scal.ambi.model.data.server.ServerResponseException
import io.scal.ambi.model.repository.data.user.IUserRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AuthProfileCheckerInteractor @Inject constructor(private val localUserDataRepository: ILocalUserDataRepository,
                                                       private val userRepository: IUserRepository) : IAuthProfileCheckerInteractor {

    private var lastUpdateTime: Long = 0

    override fun getUserProfile(): Observable<User> {
        return localUserDataRepository.observeCurrentUser()
            .doOnNext { doUserProfileUpdate(it) }
    }

    private fun doUserProfileUpdate(user: User) {
        if (SystemClock.elapsedRealtime() - lastUpdateTime > TimeUnit.MINUTES.toMillis(1)) {
            userRepository.getProfile(user.uid)
                .subscribe(
                    {
                        Timber.i("user has been updated")
                        lastUpdateTime = SystemClock.elapsedRealtime()
                        localUserDataRepository.saveCurrentUser(it).subscribe()
                    },
                    {
                        Timber.i(it, "can not update user object")
                        if (it is ServerResponseException && (it.requiresLogin || it.notFound || it.notAuthorized)) {
                            localUserDataRepository.removeAllUserInfo().subscribe()
                        }
                    }
                )
        }
    }
}