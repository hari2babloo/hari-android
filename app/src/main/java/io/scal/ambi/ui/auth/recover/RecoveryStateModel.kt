package io.scal.ambi.ui.auth.recover

import io.scal.ambi.extensions.binding.ObservableString

sealed class RecoveryStateModel {

    open val email: ObservableString? = null

    open val errorMessage: String? = null

    open val progress = false

    open class DataInputStateModel(email: String?) : RecoveryStateModel() {
        override val email = ObservableString(email)
    }

    class DataInputErrorStateModel(override val errorMessage: String,
                                   email: String?) : DataInputStateModel(email)

    object ProgressStateModel : RecoveryStateModel() {

        override val progress = true
    }

    class Success(val emailData: String) : RecoveryStateModel()
}