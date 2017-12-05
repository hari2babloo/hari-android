package io.scal.ambi.ui.auth.recover

import android.content.Context
import android.databinding.ObservableField
import io.scal.ambi.R
import io.scal.ambi.entity.exceptions.GoodMessageException
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.interactor.auth.recover.IRecoveryInteractor
import io.scal.ambi.ui.global.base.BaseViewModel
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class RecoveryViewModel @Inject constructor(private val context: Context,
                                            private val router: Router,
                                            private val interactor: IRecoveryInteractor,
                                            private val rxSchedulersAbs: RxSchedulersAbs) : BaseViewModel() {

    val stateModel = ObservableField<RecoveryStateModel>(RecoveryStateModel.DataInputStateModel(
        null))

    fun recover() {
        val currentStateModel = stateModel.get()
        if (currentStateModel is RecoveryStateModel.DataInputStateModel) {
            stateModel.set(RecoveryStateModel.ProgressStateModel)

            val email = currentStateModel.email.get()

            interactor.recover(email)
                .compose(rxSchedulersAbs.ioToMainTransformerCompletable)
                .subscribe({ stateModel.set(RecoveryStateModel.Success(email)) },
                           {
                               val message =
                                   when (it) {
                                       is GoodMessageException -> it.goodMessage
                                       else                    -> context.getString(R.string.error_auth_wrong_email)
                                   }
                               stateModel.set(RecoveryStateModel.DataInputErrorStateModel(message, email))
                           })
        }
    }

    fun goBack() {
        router.exit()
    }
}