package io.scal.ambi.ui.home.calendar.list

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.extensions.view.getNavigationHolder
import io.scal.ambi.extensions.view.getRouter
import io.scal.ambi.model.interactor.home.calendar.CalendarListInteractor
import io.scal.ambi.model.interactor.home.calendar.ICalendarListInteractor
import io.scal.ambi.ui.global.base.BetterRouter
import ru.terrakok.cicerone.NavigatorHolder
import javax.inject.Named

@Module
abstract class CalendarListModule {

    @Binds
    @IntoMap
    @ViewModelKey(CalendarListViewModel::class)
    abstract fun bindViewModel(viewModel: CalendarListViewModel): ViewModel

    @Binds
    abstract fun bindInteractor(interactor: CalendarListInteractor): ICalendarListInteractor

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Named("localNavigationHolder")
        fun provideLocalNavigation(fragment: CalendarListFragment): NavigatorHolder =
            fragment.getNavigationHolder()

        @JvmStatic
        @Provides
        fun provideRouter(fragment: CalendarListFragment): BetterRouter =
            fragment.getRouter()
    }

}