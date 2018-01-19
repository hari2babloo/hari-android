package io.scal.ambi.model.interactor.launcher

import io.reactivex.Single

interface ILauncherInteractor {

    fun getUserNavigation(): Single<LauncherState>
}