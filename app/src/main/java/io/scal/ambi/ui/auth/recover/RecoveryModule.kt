package io.scal.ambi.ui.auth.recover

import android.app.Activity
import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.model.interactor.auth.recover.IRecoveryInteractor
import io.scal.ambi.model.interactor.auth.recover.RecoveryInteractor
import io.scal.ambi.ui.global.base.BetterRouter
import javax.inject.Named

@Module
internal abstract class RecoveryModule {

    @Binds
    abstract fun bindActivity(activity: RecoveryActivity): Activity

    @Binds
    @IntoMap
    @ViewModelKey(RecoveryViewModel::class)
    abstract fun bindViewModel(recoveryViewModel: RecoveryViewModel): ViewModel

    @Binds
    abstract fun bindInteractor(recoveryInteractor: RecoveryInteractor): IRecoveryInteractor

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideLocalNavigation(@Named("rootRouter") router: BetterRouter): BetterRouter {
            return router
        }
    }
}