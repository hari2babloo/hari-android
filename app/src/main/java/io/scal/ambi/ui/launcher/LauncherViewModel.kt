package io.scal.ambi.ui.launcher

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.model.interactor.launcher.ILauncherInteractor
import io.scal.ambi.model.interactor.launcher.LauncherState
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.global.BaseViewModel
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class LauncherViewModel @Inject constructor(private val router: Router,
                                            launcherInteractor: ILauncherInteractor) : BaseViewModel() {
    init {
        launcherInteractor
            .getUserNavigation()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { launcherState ->
                when (launcherState) {
                    LauncherState.NotLoggedIn -> router.replaceScreen(NavigateTo.LOGIN)
                    is LauncherState.LoggedIn -> router.newRootScreen(NavigateTo.HOME, launcherState.user)
                }
            }
            .addTo(disposables)
    }
}