package io.scal.ambi.ui.home.classes

import android.app.Activity
import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.ui.global.base.BetterRouter
import org.joda.time.DateTime
import javax.inject.Named

@Module
internal abstract class ClassesDetailsModule {

    @Binds
    abstract fun bindActivity(activity: ClassesDetailsActivity): Activity

    @Binds
    @IntoMap
    @ViewModelKey(ClassesDetailsViewModel::class)
    abstract fun bindViewModel(viewModel: ClassesDetailsViewModel): ViewModel

    @Module
    companion object {



        @JvmStatic
        @Provides
        @Named("classesDetails")
        fun provideClassesDetails(activity: ClassesDetailsActivity): ClassesData {
            val allChatDescriptions = activity.intent.getSerializableExtra(ClassesDetailsActivity.EXTRA_CLASS_ITEM) as? ClassesData ?:
                    ClassesData("Unknown", "", "", DateTime.now(),"","","",DateTime.now(), emptyList(),"")
            return allChatDescriptions
        }

        @JvmStatic
        @Provides
        fun provideLocalNavigation(@Named("rootRouter") router: BetterRouter): BetterRouter {
            return router
        }
    }
}