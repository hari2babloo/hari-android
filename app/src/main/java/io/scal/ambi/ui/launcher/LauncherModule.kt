package io.scal.ambi.ui.launcher

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.model.launcher.ILauncherInteractor
import io.scal.ambi.model.launcher.LauncherInteractor
import io.scal.ambi.presentation.launcher.LauncherViewModel

@Module
abstract class LauncherModule {

    @Binds
    @IntoMap
    @ViewModelKey(LauncherViewModel::class)
    abstract fun bindUsersViewModel(launcherViewModel: LauncherViewModel): ViewModel

    @Binds
    abstract fun bindLauncherInteractor(launcherInteractor: LauncherInteractor): ILauncherInteractor
}