package io.scal.ambi.model.interactor.launcher

import io.scal.ambi.entity.User

sealed class LauncherState {

    object NotLoggedIn : LauncherState()

    data class LoggedIn(val user: User) : LauncherState()
}