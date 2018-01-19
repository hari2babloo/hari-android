package io.scal.ambi.ui.launcher

import android.content.Intent
import com.ambi.work.R
import com.ambi.work.databinding.ActivityLauncherBinding
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.auth.login.LoginActivity
import io.scal.ambi.ui.global.base.activity.BaseActivity
import io.scal.ambi.ui.global.base.activity.BaseNavigator
import io.scal.ambi.ui.home.root.HomeActivity
import ru.terrakok.cicerone.Navigator
import kotlin.reflect.KClass

class LauncherActivity : BaseActivity<LauncherViewModel, ActivityLauncherBinding>() {

    override val layoutId: Int = R.layout.activity_launcher
    override val viewModelClass: KClass<LauncherViewModel> = LauncherViewModel::class

    override val navigator: Navigator by lazy {

        object : BaseNavigator(this) {

            override fun createActivityIntent(screenKey: String, data: Any?): Intent? =
                when (screenKey) {
                    NavigateTo.LOGIN -> LoginActivity.createScreen(this@LauncherActivity)
                    NavigateTo.HOME  -> HomeActivity.createScreen(this@LauncherActivity)
                    else             -> super.createActivityIntent(screenKey, data)
                }
        }
    }
}