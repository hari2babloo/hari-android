package io.scal.ambi.ui.launcher

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.model.interactor.launcher.ILauncherInteractor
import io.scal.ambi.model.interactor.launcher.LauncherInteractor
import io.scal.ambi.ui.global.base.BetterRouter
import javax.inject.Named

@Module
internal abstract class LauncherModule {

    @Binds
    @IntoMap
    @ViewModelKey(LauncherViewModel::class)
    abstract fun bindViewModel(launcherViewModel: LauncherViewModel): ViewModel

    @Binds
    abstract fun bindInteractor(launcherInteractor: LauncherInteractor): ILauncherInteractor

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideLocalNavigation(@Named("rootRouter") router: BetterRouter): BetterRouter {
            return router
        }
    }
}