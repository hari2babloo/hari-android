package io.scal.ambi.ui.auth.login

import android.content.Context
import android.databinding.ObservableField
import com.ambi.work.R
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.entity.exceptions.GoodMessageException
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.interactor.auth.login.ILoginInteractor
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val context: Context,
                                         router: BetterRouter,
                                         private val interactor: ILoginInteractor,
                                         private val rxSchedulersAbs: RxSchedulersAbs) : BaseViewModel(router) {

    val stateModel = ObservableField<LoginStateModel>(LoginStateModel.DataInputStateModel(null, null))

    init {
        if (false) {
            stateModel.set(LoginStateModel.DataInputStateModel("asdasd@babson.edu", "q1w2e3r4t5"))
            stateModel.set(LoginStateModel.DataInputStateModel("Genevieve@ambi.work", "q1w2e3r4t5y6"))
            stateModel.set(LoginStateModel.DataInputStateModel("abhatnagar2@babson.edu", "password"))
        }
    }

    fun login() {
        val currentStateModel = stateModel.get()
        if (currentStateModel is LoginStateModel.DataInputStateModel) {
            val email = currentStateModel.email.get()
            val password = currentStateModel.password.get()

            stateModel.set(LoginStateModel.ProgressStateModel(email, password))

            interactor.login(email, password)
                    .compose(rxSchedulersAbs.ioToMainTransformerCompletable)
                    .subscribe(
                            {
                                router.newRootScreen(NavigateTo.HOME)
                            },
                            {
                                handleError(it, false)

                                val message =
                                        when (it) {
                                            is GoodMessageException -> it.goodMessage
                                            else                    -> context.getString(R.string.error_auth_wrong_user_name)
                                        }
                                stateModel.set(LoginStateModel.DataInputErrorStateModel(message, email, password))
                            })
                    .addTo(disposables)
        }
    }

    fun goToRegister() {
        router.navigateTo(NavigateTo.REGISTER)
    }

    fun goToForgotPassword() {
        router.navigateTo(NavigateTo.FORGOT_PASSWORD)
    }
}