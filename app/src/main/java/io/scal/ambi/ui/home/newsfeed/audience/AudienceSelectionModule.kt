package io.scal.ambi.ui.home.newsfeed.audience

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.ui.global.base.BetterRouter
import javax.inject.Named

@Module
abstract class AudienceSelectionModule {

    @Binds
    @IntoMap
    @ViewModelKey(AudienceSelectionViewModel::class)
    abstract fun bindViewModel(viewModel: AudienceSelectionViewModel): ViewModel

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Named("selectedAudience")
        fun provideSelectedAudience(activity: AudienceSelectionActivity): Audience {
            val audience = activity.intent.getSerializableExtra(AudienceSelectionActivity.EXTRA_SELECTED_AUDIENCE) as? Audience
            return audience ?: Audience.COLLEGE_UPDATE
        }

        @JvmStatic
        @Provides
        fun provideLocalNavigation(@Named("rootRouter") router: BetterRouter): BetterRouter {
            return router
        }
    }
}