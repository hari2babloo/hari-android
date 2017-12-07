package io.scal.ambi.ui.auth.recover

import io.scal.ambi.extensions.binding.observable.ObservableString

sealed class RecoveryStateModel {

    open val email: ObservableString? = null

    open val errorMessage: String? = null

    open val progress = false

    internal open class DataInputStateModel(email: String?) : RecoveryStateModel() {
        override val email = ObservableString(email)
    }

    internal class DataInputErrorStateModel(override val errorMessage: String,
                                            email: String?) : DataInputStateModel(email)

    internal object ProgressStateModel : RecoveryStateModel() {

        override val progress = true
    }

    internal class Success(val emailData: String) : RecoveryStateModel()
}