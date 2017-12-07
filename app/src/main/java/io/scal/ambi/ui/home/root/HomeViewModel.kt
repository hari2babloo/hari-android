package io.scal.ambi.ui.home.root

import io.scal.ambi.ui.global.base.LocalCiceroneHolder
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class HomeViewModel @Inject constructor(router: Router) : BaseViewModel(router) {

    private val localCiceroneHolder = LocalCiceroneHolder()

    fun getNavigationHolder(tag: String): NavigatorHolder =
        localCiceroneHolder.getCicerone(tag).navigatorHolder
}