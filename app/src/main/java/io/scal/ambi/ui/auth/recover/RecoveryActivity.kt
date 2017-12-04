package io.scal.ambi.ui.auth.recover

import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.R
import io.scal.ambi.databinding.ActivityRecoveryBinding
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.view.enableCascade
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.presentation.auth.RecoveryStateModel
import io.scal.ambi.presentation.auth.RecoveryViewModel
import io.scal.ambi.ui.global.AlertDialogShower
import io.scal.ambi.ui.global.BaseActivity
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.AppNavigator
import javax.inject.Inject
import kotlin.reflect.KClass

class RecoveryActivity : BaseActivity<RecoveryViewModel, ActivityRecoveryBinding>() {

    override val layoutId: Int = R.layout.activity_recovery
    override val viewModelClass: KClass<RecoveryViewModel> = RecoveryViewModel::class

    @Inject
    lateinit var alertDialogShower: AlertDialogShower
    @Inject
    lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.stateModel
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .map { it != RecoveryStateModel.ProgressStateModel }
                .subscribe { binding.rootContainer.enableCascade(it) }
                .addTo(destroyDisposables)

        viewModel.stateModel
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .filter { it is RecoveryStateModel.Success }
                .map { it as RecoveryStateModel.Success }
                .subscribe {
                    alertDialogShower.showMessage(getString(R.string.auth_dialog_check_email),
                                                  getString(R.string.auth_dialog_email_sent, it.emailData),
                                                  getString(R.string.dialog_ok),
                                                  { router.exit() })
                }
                .addTo(destroyDisposables)
    }

    override val navigator: Navigator by lazy {

        object : AppNavigator(this, R.id.container) {

            override fun createActivityIntent(screenKey: String, data: Any?): Intent? =
                    when (screenKey) {
                        NavigateTo.REGISTER        -> null // todo go ro register
                        NavigateTo.FORGOT_PASSWORD -> null // todo make forgot password screen
                        NavigateTo.HOME            -> null // todo go to home
                        else                       -> null
                    }

            override fun createFragment(screenKey: String, data: Any?): Fragment? {
                return null
            }
        }
    }

    companion object {

        fun createScreen(context: Context) =
                Intent(context, RecoveryActivity::class.java)
    }
}