package io.scal.ambi.ui.auth.login

import io.scal.ambi.extensions.binding.ObservableString

sealed class LoginStateModel {

    open val userName: ObservableString? = null
    open val password: ObservableString? = null

    open val errorMessage: String? = null

    open val progress = false

    open class DataInputStateModel(userName: String?, password: String?) : LoginStateModel() {
        override val userName = ObservableString(userName)
        override val password = ObservableString(password)
    }

    class DataInputErrorStateModel(override val errorMessage: String,
                                   userName: String?,
                                   password: String?) : DataInputStateModel(userName, password)

    object ProgressStateModel : LoginStateModel() {
        override val progress = true
    }
}