package io.scal.ambi.ui.profile.resume

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.model.interactor.profile.IProfileResumeInteractor
import io.scal.ambi.model.interactor.profile.ProfileResumeInteractor
import io.scal.ambi.ui.global.base.BetterRouter
import javax.inject.Named

@Module
abstract class ProfileResumeModule {

    @Binds
    @IntoMap
    @ViewModelKey(ProfileResumeViewModel::class)
    abstract fun bindViewModel(viewModel: ProfileResumeViewModel): ViewModel

    @Binds
    abstract fun bindInteractor(interactor: ProfileResumeInteractor): IProfileResumeInteractor

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideLocalNavigation(@Named("rootRouter") router: BetterRouter): BetterRouter {
            return router
        }
    }

}