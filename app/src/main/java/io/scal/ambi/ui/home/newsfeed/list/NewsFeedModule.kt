package io.scal.ambi.ui.home.newsfeed.list

import android.app.Activity
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
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
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
        fun provideActivity(newsFeedFragment: NewsFeedFragment): Activity {
            return newsFeedFragment.activity!!
        }

        @JvmStatic
        @Provides
        @Named("localNavigationHolder")
        fun provideLocalNavigation(fragment: NewsFeedFragment): NavigatorHolder =
            fragment.getNavigationHolder()

        @JvmStatic
        @Provides
        fun provideRouter(fragment: NewsFeedFragment): Router =
            fragment.getRouter()
    }
}