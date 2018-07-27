package io.scal.ambi.ui.home.root

import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import javax.inject.Inject

class HomeViewModel @Inject constructor(router: BetterRouter) : BaseViewModel(router) {

    fun openProfile() {
        router.navigateTo(NavigateTo.PROFILE_DETAILS)
    }

    fun openChat(){
        router.navigateTo(NavigateTo.CHAT)
    }

    fun openNotifications(){
        router.navigateTo(NavigateTo.NOTIFICATIONS)
    }
}