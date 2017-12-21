package io.scal.ambi.ui.global.base.activity

import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import io.scal.ambi.R
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.auth.login.LoginActivity
import ru.terrakok.cicerone.android.SupportAppNavigator

open class BaseNavigator(private val activity: FragmentActivity) : SupportAppNavigator(activity, R.id.container) {

    override fun createActivityIntent(screenKey: String, data: Any?): Intent? =
        when (screenKey) {
            NavigateTo.LOGIN -> LoginActivity.createScreen(activity)
            else             -> null
        }

    override fun createFragment(screenKey: String, data: Any?): Fragment? = null
}