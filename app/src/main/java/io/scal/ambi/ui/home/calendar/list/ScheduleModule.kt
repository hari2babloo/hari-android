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
import io.scal.ambi.ui.webview.WebViewViewModel
import ru.terrakok.cicerone.NavigatorHolder
import javax.inject.Named

@Module
abstract class ScheduleModule {

    @Binds
    @IntoMap
    @ViewModelKey(WebViewViewModel::class)
    abstract fun bindWebViewViewModel(viewModel: WebViewViewModel): ViewModel

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Named("localNavigationHolder")
        fun provideLocalNavigation(fragment: SchedulerWebViewFragment): NavigatorHolder =
            fragment.getNavigationHolder()

        @JvmStatic
        @Provides
        fun provideRouter(fragment: SchedulerWebViewFragment): BetterRouter =
            fragment.getRouter()
    }

}