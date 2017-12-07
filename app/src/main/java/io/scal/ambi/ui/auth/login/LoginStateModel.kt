package io.scal.ambi.ui.auth.login

import io.scal.ambi.extensions.binding.observable.ObservableString

sealed class LoginStateModel {

    open val userName: ObservableString? = null
    open val password: ObservableString? = null

    open val errorMessage: String? = null

    open val progress = false

    internal open class DataInputStateModel(userName: String?, password: String?) : LoginStateModel() {
        override val userName = ObservableString(userName)
        override val password = ObservableString(password)
    }

    internal class DataInputErrorStateModel(override val errorMessage: String,
                                            userName: String?,
                                            password: String?) : DataInputStateModel(userName, password)

    internal object ProgressStateModel : LoginStateModel() {
        override val progress = true
    }
}