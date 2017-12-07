package io.scal.ambi.ui.auth.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.R
import io.scal.ambi.databinding.ActivityLoginBinding
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.view.enableCascade
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.auth.recover.RecoveryActivity
import io.scal.ambi.ui.global.base.activity.BaseActivity
import io.scal.ambi.ui.home.root.HomeActivity
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.android.SupportAppNavigator
import kotlin.reflect.KClass

class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>() {

    override val layoutId: Int = R.layout.activity_login
    override val viewModelClass: KClass<LoginViewModel> = LoginViewModel::class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.stateModel
            .toObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .map { it != LoginStateModel.ProgressStateModel }
            .subscribe { binding.rootContainer.enableCascade(it) }
            .addTo(destroyDisposables)
    }

    override val navigator: Navigator by lazy {

        object : SupportAppNavigator(this, R.id.container) {

            override fun createActivityIntent(screenKey: String, data: Any?): Intent? =
                when (screenKey) {
                    NavigateTo.REGISTER        -> null // todo go ro register
                    NavigateTo.FORGOT_PASSWORD -> RecoveryActivity.createScreen(this@LoginActivity)
                    NavigateTo.HOME            -> HomeActivity.createScreen(this@LoginActivity)
                    else                       -> null
                }

            override fun createFragment(screenKey: String, data: Any?): Fragment? {
                return null
            }
        }
    }

    companion object {

        fun createScreen(context: Context) =
            Intent(context, LoginActivity::class.java)
    }
}