package io.scal.ambi.ui.home.calendar.list

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.extensions.view.getNavigationHolder
import io.scal.ambi.extensions.view.getRouter
import io.scal.ambi.ui.global.base.BetterRouter
import ru.terrakok.cicerone.NavigatorHolder
import javax.inject.Named

@Module
abstract class CalendarListWebModule {

    @Binds
    @IntoMap
    @ViewModelKey(CalendarListWebViewViewModel::class)
    abstract fun bindWebViewViewModel(viewModel: CalendarListWebViewViewModel): ViewModel

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Named("localNavigationHolder")
        fun provideLocalNavigation(fragment: CalendarListWebViewFragment): NavigatorHolder =
            fragment.getNavigationHolder()

        @JvmStatic
        @Provides
        fun provideRouter(fragment: CalendarListWebViewFragment): BetterRouter =
            fragment.getRouter()
    }

}