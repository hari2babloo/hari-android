package io.scal.ambi.ui.home.more

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.extensions.view.getNavigationHolder
import io.scal.ambi.ui.global.base.BetterRouter
import ru.terrakok.cicerone.NavigatorHolder
import javax.inject.Named

@Module
internal abstract class MoreModule {

    @Binds
    @IntoMap
    @ViewModelKey(MoreViewModel::class)
    abstract fun bindViewModel(viewModel: MoreViewModel): ViewModel

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Named("localNavigationHolder")
        fun provideLocalNavigation(fragment: MoreFragment): NavigatorHolder =
                fragment.getNavigationHolder()

        @JvmStatic
        @Provides
        fun provideRouter(@Named("rootRouter") router: BetterRouter): BetterRouter {
            return router
        }
    }
}