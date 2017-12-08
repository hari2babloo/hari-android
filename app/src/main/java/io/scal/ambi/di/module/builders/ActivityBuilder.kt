package io.scal.ambi.di.module.builders

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.scal.ambi.ui.auth.login.LoginActivity
import io.scal.ambi.ui.auth.login.LoginModule
import io.scal.ambi.ui.auth.profile.AuthProfileModule
import io.scal.ambi.ui.auth.recover.RecoveryActivity
import io.scal.ambi.ui.auth.recover.RecoveryModule
import io.scal.ambi.ui.home.newsfeed.audience.AudienceSelectionActivity
import io.scal.ambi.ui.home.newsfeed.audience.AudienceSelectionModule
import io.scal.ambi.ui.home.root.HomeActivity
import io.scal.ambi.ui.home.root.HomeModule
import io.scal.ambi.ui.launcher.LauncherActivity
import io.scal.ambi.ui.launcher.LauncherModule

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [LauncherModule::class])
    abstract fun bindLauncherActivity(): LauncherActivity

    @ContributesAndroidInjector(modules = [LoginModule::class])
    abstract fun bindLoginActivity(): LoginActivity

    @ContributesAndroidInjector(modules = [RecoveryModule::class])
    abstract fun bindRecoveryActivity(): RecoveryActivity

    @ContributesAndroidInjector(modules = [HomeModule::class, AuthProfileModule::class])
    abstract fun bindHomeActivity(): HomeActivity

    @ContributesAndroidInjector(modules = [AudienceSelectionModule::class])
    abstract fun bindAudienceSelectionActivity(): AudienceSelectionActivity
}