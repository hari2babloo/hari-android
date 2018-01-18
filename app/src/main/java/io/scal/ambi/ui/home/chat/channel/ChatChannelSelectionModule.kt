package io.scal.ambi.ui.home.chat.channel

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import com.ambi.work.R
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.entity.chat.ChatChannelDescription
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.ui.global.base.BetterRouter
import org.joda.time.DateTime
import javax.inject.Named

@Module
abstract class ChatChannelSelectionModule {

    @Binds
    @IntoMap
    @ViewModelKey(ChatChannelSelectionViewModel::class)
    abstract fun bindViewModel(viewModel: ChatChannelSelectionViewModel): ViewModel

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Named("selectedChatDescription")
        fun provideSelectedChatDescription(activity: ChatChannelSelectionActivity): ChatChannelDescription {
            val chatDescription = activity.intent.getSerializableExtra(ChatChannelSelectionActivity.EXTRA_SELECTED_CHAT) as? ChatChannelDescription
            if (null == chatDescription) {
                activity.finish()
            }
            return chatDescription ?: ChatChannelDescription("", "", IconImage(R.drawable.ic_ambi_logo), DateTime.now())
        }

        @JvmStatic
        @Provides
        @Named("allChatDescriptions")
        fun provideAllChatDescriptions(activity: ChatChannelSelectionActivity): List<ChatChannelDescription> {
            val allChatDescriptions = activity.intent.getSerializableExtra(ChatChannelSelectionActivity.EXTRA_ALL_CHATS) as? List<*>
            val selectedChat = provideSelectedChatDescription(activity)
            if (null == allChatDescriptions || !allChatDescriptions.contains(selectedChat)) {
                activity.finish()
                return listOf(selectedChat)
            }
            return allChatDescriptions.mapNotNull { it as? ChatChannelDescription }
        }

        @JvmStatic
        @Provides
        fun provideLocalNavigation(@Named("rootRouter") router: BetterRouter): BetterRouter {
            return router
        }
    }
}