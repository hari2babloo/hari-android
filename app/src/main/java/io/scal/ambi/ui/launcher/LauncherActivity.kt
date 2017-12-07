package io.scal.ambi.ui.launcher

import android.content.Intent
import android.support.v4.app.Fragment
import io.scal.ambi.R
import io.scal.ambi.databinding.ActivityLauncherBinding
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.auth.login.LoginActivity
import io.scal.ambi.ui.global.base.activity.BaseActivity
import io.scal.ambi.ui.home.root.HomeActivity
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.android.SupportAppNavigator
import kotlin.reflect.KClass

class LauncherActivity : BaseActivity<LauncherViewModel, ActivityLauncherBinding>() {

    override val layoutId: Int = R.layout.activity_launcher
    override val viewModelClass: KClass<LauncherViewModel> = LauncherViewModel::class

    override val navigator: Navigator by lazy {

        object : SupportAppNavigator(this, R.id.container) {

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