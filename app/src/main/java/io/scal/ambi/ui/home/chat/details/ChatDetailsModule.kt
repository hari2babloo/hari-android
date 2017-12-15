package io.scal.ambi.ui.home.chat.details

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import ru.terrakok.cicerone.Router
import javax.inject.Named

@Module
abstract class ChatDetailsModule {

    @Binds
    @IntoMap
    @ViewModelKey(ChatDetailsViewModel::class)
    abstract fun bindViewModel(viewModel: ChatDetailsViewModel): ViewModel

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Named("chatUid")
        fun provideChatUid(activity: ChatDetailsActivity): String {
            val chatUid = activity.intent.getSerializableExtra(ChatDetailsActivity.EXTRA_CHAT_UID) as? String
            if (null == chatUid) {
                activity.finish()
            }
            return chatUid ?: ""
        }

        @JvmStatic
        @Provides
        fun provideLocalNavigation(@Named("rootRouter") router: Router): Router {
            return router
        }
    }

}