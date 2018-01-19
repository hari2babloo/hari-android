package io.scal.ambi.di.module.builders

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.scal.ambi.ui.auth.login.LoginActivity
import io.scal.ambi.ui.auth.login.LoginModule
import io.scal.ambi.ui.auth.profile.AuthProfileModule
import io.scal.ambi.ui.auth.recover.RecoveryActivity
import io.scal.ambi.ui.auth.recover.RecoveryModule
import io.scal.ambi.ui.global.picker.PickerModule
import io.scal.ambi.ui.home.chat.channel.ChatChannelSelectionActivity
import io.scal.ambi.ui.home.chat.channel.ChatChannelSelectionModule
import io.scal.ambi.ui.home.chat.details.ChatDetailsActivity
import io.scal.ambi.ui.home.chat.details.ChatDetailsModule
import io.scal.ambi.ui.home.chat.newmessage.ChatNewMessageActivity
import io.scal.ambi.ui.home.chat.newmessage.ChatNewMessageModule
import io.scal.ambi.ui.home.newsfeed.audience.AudienceSelectionActivity
import io.scal.ambi.ui.home.newsfeed.audience.AudienceSelectionModule
import io.scal.ambi.ui.home.newsfeed.creation.FeedItemCreationActivity
import io.scal.ambi.ui.home.newsfeed.creation.FeedItemCreationModule
import io.scal.ambi.ui.home.root.HomeActivity
import io.scal.ambi.ui.home.root.HomeModule
import io.scal.ambi.ui.launcher.LauncherActivity
import io.scal.ambi.ui.launcher.LauncherModule
import io.scal.ambi.ui.profile.details.ProfileDetailsActivity
import io.scal.ambi.ui.profile.details.ProfileDetailsModule
import io.scal.ambi.ui.profile.resume.ProfileResumeActivity
import io.scal.ambi.ui.profile.resume.ProfileResumeModule

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [LauncherModule::class])
    abstract fun bindLauncherActivity(): LauncherActivity

    @ContributesAndroidInjector(modules = [LoginModule::class])
    abstract fun bindLoginActivity(): LoginActivity

    @ContributesAndroidInjector(modules = [RecoveryModule::class])
    abstract fun bindRecoveryActivity(): RecoveryActivity

    @ContributesAndroidInjector(modules = [HomeModule::class, AuthProfileModule::class])
    abstract fun bindHomeActivity(): HomeActivity

    @ContributesAndroidInjector(modules = [AudienceSelectionModule::class])
    abstract fun bindAudienceSelectionActivity(): AudienceSelectionActivity

    @ContributesAndroidInjector(modules = [FeedItemCreationModule::class, AuthProfileModule::class, PickerModule::class])
    abstract fun bindFeedItemCreationActivity(): FeedItemCreationActivity

    @ContributesAndroidInjector(modules = [ChatDetailsModule::class, AuthProfileModule::class, PickerModule::class])
    abstract fun bindChatDetailsActivity(): ChatDetailsActivity

    @ContributesAndroidInjector(modules = [ChatNewMessageModule::class, AuthProfileModule::class])
    abstract fun bindChatNewActivity(): ChatNewMessageActivity

    @ContributesAndroidInjector(modules = [ChatChannelSelectionModule::class, AuthProfileModule::class])
    abstract fun bindChatChannelSelectionActivity(): ChatChannelSelectionActivity

    @ContributesAndroidInjector(modules = [ProfileDetailsModule::class, AuthProfileModule::class, PickerModule::class])
    abstract fun bindProfileDetailsActivity(): ProfileDetailsActivity

    @ContributesAndroidInjector(modules = [ProfileResumeModule::class, AuthProfileModule::class])
    abstract fun bindProfileResumeActivity(): ProfileResumeActivity
}