package io.scal.ambi.di.module.builders

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.scal.ambi.ui.home.calendar.list.CalendarListFragment
import io.scal.ambi.ui.home.calendar.list.CalendarListModule
import io.scal.ambi.ui.home.chat.list.ChatListFragment
import io.scal.ambi.ui.home.chat.list.ChatListModule
import io.scal.ambi.ui.home.newsfeed.creation.base.CreationBottomModule
import io.scal.ambi.ui.home.newsfeed.creation.polls.PollsCreationFragment
import io.scal.ambi.ui.home.newsfeed.creation.polls.PollsCreationModule
import io.scal.ambi.ui.home.newsfeed.creation.status.StatusUpdateFragment
import io.scal.ambi.ui.home.newsfeed.creation.status.StatusUpdateModule
import io.scal.ambi.ui.home.newsfeed.list.NewsFeedFragment
import io.scal.ambi.ui.home.newsfeed.list.NewsFeedModule

@Module
abstract class FragmentBuilder {

    @ContributesAndroidInjector(modules = [NewsFeedModule::class])
    abstract fun bindNewsFeedFragment(): NewsFeedFragment

    @ContributesAndroidInjector(modules = [PollsCreationModule::class, CreationBottomModule::class])
    abstract fun bindPollsCreationFragment(): PollsCreationFragment

    @ContributesAndroidInjector(modules = [StatusUpdateModule::class, CreationBottomModule::class])
    abstract fun bindStatusUpdateFragment(): StatusUpdateFragment

    @ContributesAndroidInjector(modules = [ChatListModule::class])
    abstract fun bindChatListFragment(): ChatListFragment

    @ContributesAndroidInjector(modules = [CalendarListModule::class])
    abstract fun bindCalendarListFragment(): CalendarListFragment
}