package io.scal.ambi.ui.home.chat.details

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import com.ambi.work.R
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.entity.chat.ChatChannelDescription
import io.scal.ambi.entity.chat.PreviewChatItem
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.model.interactor.home.chat.ChatDetailsInteractor
import io.scal.ambi.model.interactor.home.chat.IChatDetailsInteractor
import io.scal.ambi.ui.global.base.BetterRouter
import org.joda.time.DateTime
import javax.inject.Named

@Module
abstract class ChatDetailsModule {

    @Binds
    @IntoMap
    @ViewModelKey(ChatDetailsViewModel::class)
    abstract fun bindViewModel(viewModel: ChatDetailsViewModel): ViewModel

    @Binds
    abstract fun bindInteractor(interactor: ChatDetailsInteractor): IChatDetailsInteractor

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Named("chatDescription")
        fun provideChatUid(activity: ChatDetailsActivity): ChatChannelDescription {
            val chatDescription = activity.intent.getSerializableExtra(ChatDetailsActivity.EXTRA_CHAT_DESCRIPTION) as? ChatChannelDescription
            if (null == chatDescription) {
                activity.finish()
            }
            return chatDescription ?: ChatChannelDescription("", "", IconImage(R.drawable.ic_ambi_logo), DateTime.now())
        }

        @JvmStatic
        @Provides
        @Named("chatInfo")
        fun provideChatInfo(activity: ChatDetailsActivity): PreviewChatItem? =
            activity.intent.getSerializableExtra(ChatDetailsActivity.EXTRA_CHAT_INFO) as? PreviewChatItem

        @JvmStatic
        @Provides
        fun provideLocalNavigation(@Named("rootRouter") router: BetterRouter): BetterRouter {
            return router
        }
    }

}