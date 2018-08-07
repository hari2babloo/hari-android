package io.scal.ambi.ui.global.base.activity

import android.content.Intent
import android.support.annotation.CallSuper
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.ambi.work.R
import com.crashlytics.android.Crashlytics
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.auth.login.LoginActivity
import io.scal.ambi.ui.global.base.fragment.BaseNavigationFragment
import io.scal.ambi.ui.home.chat.list.ChatListActivity
import io.scal.ambi.ui.home.notifications.NotificationsActivity
import io.scal.ambi.ui.profile.details.ProfileDetailsActivity
import ru.terrakok.cicerone.android.SupportAppNavigator
import ru.terrakok.cicerone.commands.Command
import timber.log.Timber
import java.io.PrintWriter
import java.io.StringWriter

open class BaseNavigator(private val activity: FragmentActivity) : SupportAppNavigator(activity, R.id.container) {

    constructor(fragmentActivity: BaseNavigationFragment<*, *>) : this(fragmentActivity.activity!!)

    override fun applyCommand(command: Command) {
        try {
            super.applyCommand(command)
        } catch (exception: Exception) {
            Timber.e(exception, "error in navigation! we can not execute $command")
        }
    }

    @CallSuper
    override fun createActivityIntent(screenKey: String, data: Any?): Intent? =
        when (screenKey) {
            NavigateTo.LOGIN           -> LoginActivity.createScreen(activity)
            NavigateTo.PROFILE_DETAILS ->
                if (data is String) ProfileDetailsActivity.createScreen(activity, data)
                else ProfileDetailsActivity.createScreen(activity)
            NavigateTo.CHAT            -> ChatListActivity.createScreen(activity)
            NavigateTo.NOTIFICATIONS   -> NotificationsActivity.createScreen(activity)
            else                       -> null
        }

    override fun createFragment(screenKey: String, data: Any?): Fragment? = null

    override fun unknownScreen(command: Command) {
        activity

        val sw = StringWriter()
        val pw = PrintWriter(sw)
        Throwable().printStackTrace(pw)
        val sStackTrace = sw.toString() // stack trace as a string

        val navigationException = RuntimeException("Can't create a screen for passed screenKey: $command. with trace: $sStackTrace")
        Crashlytics.logException(navigationException)
        Timber.w(navigationException, "navigation error")
    }
}