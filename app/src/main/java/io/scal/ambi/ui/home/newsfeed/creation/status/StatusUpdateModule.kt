package io.scal.ambi.ui.home.newsfeed.creation.status

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.extensions.view.getNavigationHolder
import io.scal.ambi.model.interactor.home.newsfeed.creation.IStatusUpdateInteractor
import io.scal.ambi.model.interactor.home.newsfeed.creation.StatusUpdateInteractor
import io.scal.ambi.ui.global.base.BetterRouter
import ru.terrakok.cicerone.NavigatorHolder
import javax.inject.Named

@Module
abstract class StatusUpdateModule {

    @Binds
    @IntoMap
    @ViewModelKey(StatusUpdateViewModel::class)
    abstract fun bindViewModel(viewModel: StatusUpdateViewModel): ViewModel

    @Binds
    abstract fun bindInteractor(interactor: StatusUpdateInteractor): IStatusUpdateInteractor

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Named("localNavigationHolder")
        fun provideLocalNavigation(fragment: StatusUpdateFragment): NavigatorHolder =
            fragment.getNavigationHolder()

        @JvmStatic
        @Provides
        fun provideRouter(@Named("rootRouter") router: BetterRouter): BetterRouter {
            return router
        }
    }
}