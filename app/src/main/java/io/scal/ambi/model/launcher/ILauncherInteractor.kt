package io.scal.ambi.model.launcher

import io.reactivex.Single

interface ILauncherInteractor {

    fun getUserNavigation(): Single<LauncherState>
}