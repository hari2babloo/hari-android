package io.scal.ambi.ui.home.classes

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
internal abstract class ClassesModule {

    @Binds
    @IntoMap
    @ViewModelKey(ClassesViewModel::class)
    abstract fun bindViewModel(viewModel: ClassesViewModel): ViewModel

    @Binds
    abstract fun bindInteractor(interactor: ClassesInteractor): IClassesInteractor

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Named("localNavigationHolder")
        fun provideLocalNavigation(fragment: ClassesFragment): NavigatorHolder =
            fragment.getNavigationHolder()

        @JvmStatic
        @Provides
        fun provideRouter(fragment: ClassesFragment): BetterRouter =
            fragment.getRouter()
    }
}