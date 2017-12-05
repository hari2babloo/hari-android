package io.scal.ambi.di.module.builders

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.scal.ambi.ui.home.newsfeed.NewsFeedFragment
import io.scal.ambi.ui.home.newsfeed.NewsFeedModule

@Module
abstract class FragmentBuilder {

    @ContributesAndroidInjector(modules = [NewsFeedModule::class])
    abstract fun bindNewsFeedFragment(): NewsFeedFragment
}