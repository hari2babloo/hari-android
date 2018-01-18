package io.scal.ambi.ui.auth.recover

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import com.ambi.work.R
import com.ambi.work.databinding.ActivityRecoveryBinding
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.view.enableCascade
import io.scal.ambi.ui.global.AlertDialogShower
import io.scal.ambi.ui.global.base.activity.BaseActivity
import javax.inject.Inject
import kotlin.reflect.KClass

class RecoveryActivity : BaseActivity<RecoveryViewModel, ActivityRecoveryBinding>() {

    override val layoutId: Int = R.layout.activity_recovery
    override val viewModelClass: KClass<RecoveryViewModel> = RecoveryViewModel::class

    @Inject
    lateinit var alertDialogShower: AlertDialogShower

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
                                              { onBackPressed() })
            }
            .addTo(destroyDisposables)
    }

    companion object {

        fun createScreen(context: Context) =
            Intent(context, RecoveryActivity::class.java)
    }
}