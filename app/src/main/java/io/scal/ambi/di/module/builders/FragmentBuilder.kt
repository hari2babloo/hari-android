package io.scal.ambi.di.module.builders

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.scal.ambi.ui.auth.profile.AuthProfileModule
import io.scal.ambi.ui.home.newsfeed.creation.polls.PollsCreationFragment
import io.scal.ambi.ui.home.newsfeed.creation.polls.PollsCreationModule
import io.scal.ambi.ui.home.newsfeed.list.NewsFeedFragment
import io.scal.ambi.ui.home.newsfeed.list.NewsFeedModule

@Module
abstract class FragmentBuilder {

    @ContributesAndroidInjector(modules = [NewsFeedModule::class, AuthProfileModule::class])
    abstract fun bindNewsFeedFragment(): NewsFeedFragment

    @ContributesAndroidInjector(modules = [PollsCreationModule::class])
    abstract fun bindPollsCreationFragment(): PollsCreationFragment
}