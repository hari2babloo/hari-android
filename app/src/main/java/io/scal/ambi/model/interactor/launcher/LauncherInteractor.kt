package io.scal.ambi.model.interactor.launcher

import io.reactivex.Single
import io.scal.ambi.entity.User
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LauncherInteractor @Inject constructor() : ILauncherInteractor {

    override fun getUserNavigation(): Single<LauncherState> {
        return Single.just<LauncherState>(LauncherState.LoggedIn(User()))
            .delay(1, TimeUnit.SECONDS)
    }
}