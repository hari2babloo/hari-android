package io.scal.ambi.ui.home.chat.new

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.model.interactor.home.chat.ChatNewMessageInteractor
import io.scal.ambi.model.interactor.home.chat.IChatNewMessageInteractor
import ru.terrakok.cicerone.Router
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
        fun provideLocalNavigation(@Named("rootRouter") router: Router): Router {
            return router
        }

    }
}