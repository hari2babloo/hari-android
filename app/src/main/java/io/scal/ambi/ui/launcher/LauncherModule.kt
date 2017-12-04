package io.scal.ambi.ui.launcher

import android.app.Activity
import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.model.interactor.launcher.ILauncherInteractor
import io.scal.ambi.model.interactor.launcher.LauncherInteractor
import io.scal.ambi.presentation.launcher.LauncherViewModel
import io.scal.ambi.ui.auth.recover.RecoveryActivity

@Module
abstract class LauncherModule {

    @Binds
    abstract fun bindActivity(activity: RecoveryActivity): Activity

    @Binds
    @IntoMap
    @ViewModelKey(LauncherViewModel::class)
    abstract fun bindViewModel(launcherViewModel: LauncherViewModel): ViewModel

    @Binds
    abstract fun bindInteractor(launcherInteractor: LauncherInteractor): ILauncherInteractor
}