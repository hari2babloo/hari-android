package io.scal.ambi.presentation.launcher

import io.reactivex.rxkotlin.addTo
import io.scal.ambi.model.launcher.ILauncherInteractor
import io.scal.ambi.model.launcher.LauncherState
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.global.BaseViewModel
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class LauncherViewModel @Inject constructor(private val router: Router,
                                            launcherInteractor: ILauncherInteractor) : BaseViewModel() {

    init {
        launcherInteractor
                .getUserNavigation()
                .subscribe { launcherState ->
                    when (launcherState) {
                        LauncherState.NotLoggedIn -> router.replaceScreen(NavigateTo.LOGIN)
                        is LauncherState.LoggedIn -> router.newRootScreen(NavigateTo.HOME, launcherState.user)
                    }
                }
                .addTo(disposables)
    }
}