package io.scal.ambi.ui.more

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.extensions.view.getNavigationHolder
import io.scal.ambi.extensions.view.getRouter
import io.scal.ambi.model.interactor.profile.IProfileDetailsInteractor
import io.scal.ambi.model.interactor.profile.ProfileDetailsInteractor
import io.scal.ambi.ui.global.base.BetterRouter
import ru.terrakok.cicerone.NavigatorHolder
import javax.inject.Named

@Module
internal abstract class MoreModule {

    @Binds
    @IntoMap
    @ViewModelKey(MoreViewModel::class)
    abstract fun bindViewModel(moreViewModel: MoreViewModel): ViewModel

    @Binds
    abstract fun bindInteractor(interactor: ProfileDetailsInteractor): IProfileDetailsInteractor

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Named("localNavigationHolder")
        fun provideLocalNavigation(fragment: MoreFragment): NavigatorHolder =
                fragment.getNavigationHolder()

        @JvmStatic
        @Provides
        fun provideRouter(fragment: MoreFragment): BetterRouter =
                fragment.getRouter()
    }

}