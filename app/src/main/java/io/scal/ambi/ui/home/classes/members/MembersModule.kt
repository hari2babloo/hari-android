package io.scal.ambi.ui.home.classes.members

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
import io.scal.ambi.ui.home.classes.about.AboutInteractor
import io.scal.ambi.ui.home.classes.about.IAboutInteractor
import org.joda.time.DateTime
import ru.terrakok.cicerone.NavigatorHolder
import javax.inject.Named

@Module
internal abstract class MembersModule {

    @Binds
    @IntoMap
    @ViewModelKey(MembersViewModel::class)
    abstract fun bindViewModel(viewModel: MembersViewModel): ViewModel

    @Binds
    abstract fun bindInteractor(interactor: AboutInteractor): IAboutInteractor


    @Module
    companion object {

        @JvmStatic
        @Provides
        @Named("aboutData")
        fun provideAboutData(activity: MembersFragment): ClassesData {
            return activity.arguments!!.getSerializable(MembersFragment.EXTRA_CLASS_ITEM) as? ClassesData ?: ClassesData("", "", "", DateTime.now(), "", "", "", DateTime.now(), emptyList(), "", emptyList())
        }

        @JvmStatic
        @Provides
        @Named("localNavigationHolder")
        fun provideLocalNavigation(fragment: MembersFragment): NavigatorHolder =
            fragment.getNavigationHolder()

        @JvmStatic
        @Provides
        fun provideRouter(fragment: MembersFragment): BetterRouter =
            fragment.getRouter()
    }
}