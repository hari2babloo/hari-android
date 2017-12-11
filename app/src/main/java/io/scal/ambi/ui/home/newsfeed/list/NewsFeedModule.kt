package io.scal.ambi.ui.home.newsfeed.list

import android.app.Activity
import android.arch.lifecycle.ViewModel
import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.model.interactor.home.newsfeed.INewsFeedInteractor
import io.scal.ambi.model.interactor.home.newsfeed.NewsFeedInteractor
import io.scal.ambi.ui.global.base.LocalNavigationHolder
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
        fun provideLocalNavigation(fragment: NewsFeedFragment): NavigatorHolder {
            var localNavigationHolder: LocalNavigationHolder? = null

            var parentFragment: Fragment? = fragment.parentFragment
            while (null != parentFragment) {
                if (parentFragment is LocalNavigationHolder) {
                    localNavigationHolder = parentFragment
                    break
                }
                parentFragment = parentFragment.parentFragment
            }

            if (null == localNavigationHolder && fragment.activity is LocalNavigationHolder) {
                localNavigationHolder = fragment.activity as LocalNavigationHolder
            }

            if (null == localNavigationHolder && fragment.activity?.application is LocalNavigationHolder) {
                localNavigationHolder = fragment.activity!!.application as LocalNavigationHolder
            }

            if (null == localNavigationHolder) {
                throw IllegalArgumentException("No injector was found for ${fragment.javaClass.canonicalName}")
            }
            return localNavigationHolder.getNavigationHolder(fragment.tag!!)
        }

        @JvmStatic
        @Provides
        fun provideRouter(fragment: NewsFeedFragment): Router {
            var localNavigationHolder: LocalNavigationHolder? = null

            var parentFragment: Fragment? = fragment.parentFragment
            while (null != parentFragment) {
                if (parentFragment is LocalNavigationHolder) {
                    localNavigationHolder = parentFragment
                    break
                }
                parentFragment = parentFragment.parentFragment
            }

            if (null == localNavigationHolder && fragment.activity is LocalNavigationHolder) {
                localNavigationHolder = fragment.activity as LocalNavigationHolder
            }

            if (null == localNavigationHolder && fragment.activity?.application is LocalNavigationHolder) {
                localNavigationHolder = fragment.activity!!.application as LocalNavigationHolder
            }

            if (null == localNavigationHolder) {
                throw IllegalArgumentException("No injector was found for ${fragment.javaClass.canonicalName}")
            }
            return localNavigationHolder.getRouter(fragment.tag!!)
        }
    }
}