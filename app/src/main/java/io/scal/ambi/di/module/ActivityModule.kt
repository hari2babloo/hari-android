package io.scal.ambi.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import io.scal.ambi.ui.launcher.LauncherActivity

@Module
open class ActivityModule {

    @Provides
    fun provideContext(activity: LauncherActivity): Context =
            activity
}