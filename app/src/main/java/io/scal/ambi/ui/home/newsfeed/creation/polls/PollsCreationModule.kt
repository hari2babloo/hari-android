package io.scal.ambi.ui.home.newsfeed.creation.polls

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.extensions.view.getNavigationHolder
import io.scal.ambi.model.interactor.home.newsfeed.creation.IPollsCreationInteractor
import io.scal.ambi.model.interactor.home.newsfeed.creation.PollsCreationInteractor
import io.scal.ambi.ui.global.base.BetterRouter
import ru.terrakok.cicerone.NavigatorHolder
import javax.inject.Named

@Module
abstract class PollsCreationModule {

    @Binds
    @IntoMap
    @ViewModelKey(PollsCreationViewModel::class)
    abstract fun bindViewModel(viewModel: PollsCreationViewModel): ViewModel

    @Binds
    abstract fun bindInteractor(interactor: PollsCreationInteractor): IPollsCreationInteractor

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Named("localNavigationHolder")
        fun provideLocalNavigation(fragment: PollsCreationFragment): NavigatorHolder =
            fragment.getNavigationHolder()

        @JvmStatic
        @Provides
        fun provideRouter(@Named("rootRouter") router: BetterRouter): BetterRouter {
            return router
        }
    }
}