package io.scal.ambi.ui.auth.login

import io.scal.ambi.extensions.binding.observable.ObservableString

sealed class LoginStateModel {

    open val email: ObservableString? = null
    open val password: ObservableString? = null

    open val errorMessage: String? = null

    open val progress = false

    internal open class DataInputStateModel(email: String?,
                                            password: String?) : LoginStateModel() {
        override val email = ObservableString(email)
        override val password = ObservableString(password)
    }

    internal class DataInputErrorStateModel(override val errorMessage: String,
                                            userName: String?,
                                            password: String?) : DataInputStateModel(userName, password)

    internal class ProgressStateModel(email: String?,
                                      password: String?) : LoginStateModel() {

        override val email = ObservableString(email)
        override val password = ObservableString(password)

        override val progress = true
    }
}