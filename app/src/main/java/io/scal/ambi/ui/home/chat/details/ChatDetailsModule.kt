package io.scal.ambi.ui.home.chat.details

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.entity.chat.SmallChatItem
import io.scal.ambi.model.interactor.home.chat.ChatDetailsInteractor
import io.scal.ambi.model.interactor.home.chat.IChatDetailsInteractor
import ru.terrakok.cicerone.Router
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
        @Named("chatUid")
        fun provideChatUid(activity: ChatDetailsActivity): String {
            val chatUid = activity.intent.getStringExtra(ChatDetailsActivity.EXTRA_CHAT_UID)
            if (null == chatUid) {
                activity.finish()
            }
            return chatUid ?: ""
        }

        @JvmStatic
        @Provides
        @Named("chatInfo")
        fun provideChatInfo(activity: ChatDetailsActivity): SmallChatItem? =
            activity.intent.getSerializableExtra(ChatDetailsActivity.EXTRA_CHAT_INFO) as SmallChatItem

        @JvmStatic
        @Provides
        fun provideLocalNavigation(@Named("rootRouter") router: Router): Router {
            return router
        }
    }

}