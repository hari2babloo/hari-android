package io.scal.ambi.di.module.builders

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.scal.ambi.ui.launcher.LauncherActivity
import io.scal.ambi.ui.launcher.LauncherModule

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [LauncherModule::class])
    abstract fun bindLauncherActivity(): LauncherActivity
}