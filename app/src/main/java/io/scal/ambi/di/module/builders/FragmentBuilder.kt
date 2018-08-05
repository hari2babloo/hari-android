package io.scal.ambi.di.module.builders

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.scal.ambi.ui.home.calendar.list.CalendarListFragment
import io.scal.ambi.ui.home.calendar.list.CalendarListModule
import io.scal.ambi.ui.home.chat.list.ChatListFragment
import io.scal.ambi.ui.home.chat.list.ChatListModule
import io.scal.ambi.ui.home.classes.ClassesFragment
import io.scal.ambi.ui.home.classes.ClassesModule
import io.scal.ambi.ui.home.classes.about.AboutFragment
import io.scal.ambi.ui.home.classes.about.AboutModule
import io.scal.ambi.ui.home.newsfeed.creation.announcement.AnnouncementCreationFragment
import io.scal.ambi.ui.home.newsfeed.creation.announcement.AnnouncementCreationModule
import io.scal.ambi.ui.home.newsfeed.creation.base.CreationBottomModule
import io.scal.ambi.ui.home.newsfeed.creation.polls.PollsCreationFragment
import io.scal.ambi.ui.home.newsfeed.creation.polls.PollsCreationModule
import io.scal.ambi.ui.home.newsfeed.creation.status.StatusUpdateFragment
import io.scal.ambi.ui.home.newsfeed.creation.status.StatusUpdateModule
import io.scal.ambi.ui.home.newsfeed.list.NewsFeedFragment
import io.scal.ambi.ui.home.newsfeed.list.NewsFeedModule
import io.scal.ambi.ui.webview.*

@Module
abstract class FragmentBuilder {

    @ContributesAndroidInjector(modules = [NewsFeedModule::class])
    abstract fun bindNewsFeedFragment(): NewsFeedFragment

    @ContributesAndroidInjector(modules = [PollsCreationModule::class, CreationBottomModule::class])
    abstract fun bindPollsCreationFragment(): PollsCreationFragment

    @ContributesAndroidInjector(modules = [StatusUpdateModule::class, CreationBottomModule::class])
    abstract fun bindStatusUpdateFragment(): StatusUpdateFragment

    @ContributesAndroidInjector(modules = [AnnouncementCreationModule::class, CreationBottomModule::class])
    abstract fun bindAnnouncementCreationFragment(): AnnouncementCreationFragment

    @ContributesAndroidInjector(modules = [ChatListModule::class])
    abstract fun bindChatListFragment(): ChatListFragment

    @ContributesAndroidInjector(modules = [CalendarListModule::class])
    abstract fun bindCalendarListFragment(): CalendarListFragment

    @ContributesAndroidInjector(modules = [WebViewModule::class])
    abstract fun bindWebViewFragment(): WebViewFragment

    @ContributesAndroidInjector(modules = [ScheduleModule::class])
    abstract fun bindSchedulerFragment(): SchedulerWebViewFragment

    @ContributesAndroidInjector(modules = [ResourceModule::class])
    abstract fun bindResourcesFragment(): ResourceWebViewFragment

    @ContributesAndroidInjector(modules = [ClassesModule::class])
    abstract fun bindClassesFragment(): ClassesFragment

    @ContributesAndroidInjector(modules = [AboutModule::class])
    abstract fun bindAboutFragment(): AboutFragment
}