package io.scal.ambi.ui.home.newsfeed.creation.announcement

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.extensions.view.getNavigationHolder
import io.scal.ambi.model.interactor.home.newsfeed.creation.AnnouncementCreationInteractor
import io.scal.ambi.model.interactor.home.newsfeed.creation.IAnnouncementCreationInteractor
import io.scal.ambi.ui.global.base.BetterRouter
import ru.terrakok.cicerone.NavigatorHolder
import javax.inject.Named

@Module
abstract class AnnouncementCreationModule {

    @Binds
    @IntoMap
    @ViewModelKey(AnnouncementCreationViewModel::class)
    abstract fun bindViewModel(viewModel: AnnouncementCreationViewModel): ViewModel

    @Binds
    abstract fun bindInteractor(interactor: AnnouncementCreationInteractor): IAnnouncementCreationInteractor

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Named("localNavigationHolder")
        fun provideLocalNavigation(fragment: AnnouncementCreationFragment): NavigatorHolder =
            fragment.getNavigationHolder()

        @JvmStatic
        @Provides
        fun provideRouter(@Named("rootRouter") router: BetterRouter): BetterRouter {
            return router
        }
    }
}