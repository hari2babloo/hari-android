package io.scal.ambi.model.interactor.launcher

import io.scal.ambi.entity.user.User

sealed class LauncherState {

    object NotLoggedIn : LauncherState()

    data class LoggedIn(val user: User) : LauncherState()
}