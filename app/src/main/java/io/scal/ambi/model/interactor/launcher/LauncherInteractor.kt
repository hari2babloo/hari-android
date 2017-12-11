package io.scal.ambi.model.interactor.launcher

import io.reactivex.Single
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import javax.inject.Inject

class LauncherInteractor @Inject constructor(private val localUserDataRepository: ILocalUserDataRepository) : ILauncherInteractor {

    override fun getUserNavigation(): Single<LauncherState> {
        return Single.fromCallable {
            localUserDataRepository.getCurrentUser()
                ?.let { LauncherState.LoggedIn(it) }
                ?: LauncherState.NotLoggedIn
        }
    }
}