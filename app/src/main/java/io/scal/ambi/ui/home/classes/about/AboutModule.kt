package io.scal.ambi.ui.home.classes.about

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.extensions.view.getNavigationHolder
import io.scal.ambi.extensions.view.getRouter
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.home.classes.ClassesData
import org.joda.time.DateTime
import ru.terrakok.cicerone.NavigatorHolder
import javax.inject.Named

@Module
internal abstract class AboutModule {

    @Binds
    @IntoMap
    @ViewModelKey(AboutViewModel::class)
    abstract fun bindViewModel(viewModel: AboutViewModel): ViewModel

    @Binds
    abstract fun bindInteractor(interactor: AboutInteractor): IAboutInteractor


    @Module
    companion object {

        @JvmStatic
        @Provides
        @Named("aboutData")
        fun provideAboutData(activity: AboutFragment): ClassesData {
            return activity.arguments!!.getSerializable(AboutFragment.EXTRA_CLASS_ITEM) as? ClassesData ?: ClassesData("","","",DateTime.now(),"","","",DateTime.now(), emptyList(),"")
        }

        @JvmStatic
        @Provides
        @Named("localNavigationHolder")
        fun provideLocalNavigation(fragment: AboutFragment): NavigatorHolder =
            fragment.getNavigationHolder()

        @JvmStatic
        @Provides
        fun provideRouter(fragment: AboutFragment): BetterRouter =
            fragment.getRouter()
    }
}