package io.scal.ambi.ui.home.newsfeed.list

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.extensions.view.getNavigationHolder
import io.scal.ambi.extensions.view.getRouter
import io.scal.ambi.model.interactor.home.newsfeed.INewsFeedInteractor
import io.scal.ambi.model.interactor.home.newsfeed.NewsFeedInteractor
import io.scal.ambi.ui.global.base.BetterRouter
import ru.terrakok.cicerone.NavigatorHolder
import javax.inject.Named

@Module
internal abstract class NewsFeedModule {

    @Binds
    @IntoMap
    @ViewModelKey(NewsFeedViewModel::class)
    abstract fun bindViewModel(viewModel: NewsFeedViewModel): ViewModel

    @Binds
    abstract fun bindInteractor(interactor: NewsFeedInteractor): INewsFeedInteractor

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Named("localNavigationHolder")
        fun provideLocalNavigation(fragment: NewsFeedFragment): NavigatorHolder =
            fragment.getNavigationHolder()

        @JvmStatic
        @Provides
        fun provideRouter(fragment: NewsFeedFragment): BetterRouter =
            fragment.getRouter()
    }
}