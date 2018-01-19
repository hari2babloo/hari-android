package io.scal.ambi.ui.home.chat.newmessage

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.model.interactor.home.chat.ChatNewMessageInteractor
import io.scal.ambi.model.interactor.home.chat.IChatNewMessageInteractor
import io.scal.ambi.ui.global.base.BetterRouter
import javax.inject.Named

@Module
abstract class ChatNewMessageModule {

    @Binds
    @IntoMap
    @ViewModelKey(ChatNewMessageViewModel::class)
    abstract fun bindViewModel(viewModel: ChatNewMessageViewModel): ViewModel

    @Binds
    abstract fun bindInteractor(interactor: ChatNewMessageInteractor): IChatNewMessageInteractor

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideAppendingData(activity: ChatNewMessageActivity): AppendingData {
            return activity.intent.getSerializableExtra(ChatNewMessageActivity.EXTRA_APPENDING_DATA) as? AppendingData ?: AppendingData.NOTHING
        }

        @JvmStatic
        @Provides
        fun provideLocalNavigation(@Named("rootRouter") router: BetterRouter): BetterRouter {
            return router
        }
    }
}