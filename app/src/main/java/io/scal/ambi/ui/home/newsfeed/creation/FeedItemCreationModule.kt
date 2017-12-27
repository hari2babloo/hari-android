package io.scal.ambi.ui.home.newsfeed.creation

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.ui.global.base.BetterRouter
import javax.inject.Named

@Module
abstract class FeedItemCreationModule {

    @Binds
    @IntoMap
    @ViewModelKey(FeedItemCreationViewModel::class)
    abstract fun bindViewModel(viewModel: FeedItemCreationViewModel): ViewModel

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Named("selectedFeedItemCreation")
        fun provideSelectedAudience(activity: FeedItemCreationActivity): FeedItemCreation {
            val feedItemCreation = activity.intent.getSerializableExtra(FeedItemCreationActivity.EXTRA_SELECTED_CREATION) as? FeedItemCreation
            return feedItemCreation ?: FeedItemCreation.STATUS
        }

        @JvmStatic
        @Provides
        fun provideLocalNavigation(@Named("rootRouter") router: BetterRouter): BetterRouter {
            return router
        }
    }

}