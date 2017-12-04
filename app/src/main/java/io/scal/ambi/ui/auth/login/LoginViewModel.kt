package io.scal.ambi.ui.auth.login

import android.content.Context
import android.databinding.ObservableField
import io.scal.ambi.R
import io.scal.ambi.entity.exceptions.GoodMessageException
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.interactor.auth.login.ILoginInteractor
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.global.BaseViewModel
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val context: Context,
                                         private val router: Router,
                                         private val interactor: ILoginInteractor,
                                         private val rxSchedulersAbs: RxSchedulersAbs) : BaseViewModel() {

    val stateModel = ObservableField<LoginStateModel>(LoginStateModel.DataInputStateModel(null,
                                                                                                                                                null))

    fun login() {
        val currentStateModel = stateModel.get()
        if (currentStateModel is LoginStateModel.DataInputStateModel) {
            stateModel.set(LoginStateModel.ProgressStateModel)

            val userName = currentStateModel.userName.get()
            interactor.login(userName, currentStateModel.password.get())
                .compose(rxSchedulersAbs.ioToMainTransformerCompletable)
                .subscribe({ router.newRootScreen(NavigateTo.HOME) },
                           {
                               val message =
                                   when (it) {
                                       is GoodMessageException -> it.goodMessage
                                       else                    -> context.getString(R.string.error_auth_wrong_user_name)
                                   }
                               stateModel.set(LoginStateModel.DataInputErrorStateModel(message, userName, null))
                           })
        }
    }

    fun goToRegister() {
        router.navigateTo(NavigateTo.REGISTER)
    }

    fun goToForgotPassword() {
        router.navigateTo(NavigateTo.FORGOT_PASSWORD)
    }
}