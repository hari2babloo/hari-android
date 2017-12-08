package io.scal.ambi.ui.home.root

import android.app.Activity
import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import ru.terrakok.cicerone.Router
import javax.inject.Named


@Module
internal abstract class HomeModule {

    @Binds
    abstract fun bindActivity(activity: HomeActivity): Activity

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindViewModel(viewModel: HomeViewModel): ViewModel

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideLocalNavigation(@Named("rootRouter") router: Router): Router {
            return router
        }
    }
}