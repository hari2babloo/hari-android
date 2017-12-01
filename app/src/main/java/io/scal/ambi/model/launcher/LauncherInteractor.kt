package io.scal.ambi.model.launcher

import io.reactivex.Single
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LauncherInteractor @Inject constructor() : ILauncherInteractor {

    override fun getUserNavigation(): Single<LauncherState> {
        return Single.just<LauncherState>(LauncherState.NotLoggedIn)
                .delay(5, TimeUnit.SECONDS)
    }
}