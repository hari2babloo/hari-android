package io.scal.ambi.ui.home.notifications

import android.app.Activity
import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.ui.global.base.BetterRouter
import javax.inject.Named

@Module
internal abstract class NotificationsModule {

    @Binds
    abstract fun bindActivity(activity: NotificationsActivity): Activity

    @Binds
    @IntoMap
    @ViewModelKey(NotificationsViewModel::class)
    abstract fun bindViewModel(viewModel: NotificationsViewModel): ViewModel

    @Binds
    abstract fun bindInteractor(interactor: NotificationInteractor): INotificationsInteractor

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideLocalNavigation(@Named("rootRouter") router: BetterRouter): BetterRouter {
            return router
        }
    }
}