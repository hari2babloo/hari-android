package io.scal.ambi.ui.profile.details

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.model.interactor.profile.IProfileDetailsInteractor
import io.scal.ambi.model.interactor.profile.ProfileDetailsInteractor
import io.scal.ambi.ui.global.base.BetterRouter
import javax.inject.Named

@Module
abstract class ProfileDetailsModule {

    @Binds
    @IntoMap
    @ViewModelKey(ProfileDetailsViewModel::class)
    abstract fun bindViewModel(viewModel: ProfileDetailsViewModel): ViewModel

    @Binds
    abstract fun bindInteractor(interactor: ProfileDetailsInteractor): IProfileDetailsInteractor

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideAppendingData(activity: ProfileDetailsActivity): String {
            return activity.intent.getSerializableExtra(ProfileDetailsActivity.EXTRA_PROFILE_UID) as? String ?: ""
        }

        @JvmStatic
        @Provides
        fun provideLocalNavigation(@Named("rootRouter") router: BetterRouter): BetterRouter {
            return router
        }
    }
}