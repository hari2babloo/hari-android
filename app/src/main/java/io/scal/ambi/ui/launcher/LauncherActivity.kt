package io.scal.ambi.ui.launcher

import android.app.Fragment
import android.content.Intent
import io.scal.ambi.R
import io.scal.ambi.databinding.ActivityLauncherBinding
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.auth.login.LoginActivity
import io.scal.ambi.ui.global.BaseActivity
import io.scal.ambi.ui.home.HomeActivity
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.android.AppNavigator
import kotlin.reflect.KClass

class LauncherActivity : BaseActivity<LauncherViewModel, ActivityLauncherBinding>() {

    override val layoutId: Int = R.layout.activity_launcher
    override val viewModelClass: KClass<LauncherViewModel> = LauncherViewModel::class

    override val navigator: Navigator by lazy {

        object : AppNavigator(this, R.id.container) {

            override fun createActivityIntent(screenKey: String, data: Any?): Intent? =
                when (screenKey) {
                    NavigateTo.LOGIN -> LoginActivity.createScreen(this@LauncherActivity)
                    NavigateTo.HOME  -> HomeActivity.createScreen(this@LauncherActivity)
                    else             -> null
                }

            override fun createFragment(screenKey: String, data: Any?): Fragment? {
                return null
            }
        }
    }
}