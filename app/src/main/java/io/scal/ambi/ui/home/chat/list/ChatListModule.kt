package io.scal.ambi.ui.home.chat.list

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.model.interactor.home.chat.ChatListInteractor
import io.scal.ambi.model.interactor.home.chat.IChatListInteractor
import io.scal.ambi.ui.global.base.BetterRouter
import javax.inject.Named

@Module
abstract class ChatListModule {

    @Binds
    @IntoMap
    @ViewModelKey(ChatListViewModel::class)
    abstract fun bindViewModel(viewModel: ChatListViewModel): ViewModel

    @Binds
    abstract fun bindInteractor(interactor: ChatListInteractor): IChatListInteractor

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideAppendingData(activity: ChatListActivity): String {
            return activity.intent.getSerializableExtra(ChatListActivity.EXTRA_PROFILE_UID) as? String ?: ""
        }

        @JvmStatic
        @Provides
        fun provideLocalNavigation(@Named("rootRouter") router: BetterRouter): BetterRouter {
            return router
        }
    }

}