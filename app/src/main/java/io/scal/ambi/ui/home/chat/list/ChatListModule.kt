package io.scal.ambi.ui.home.chat.list

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.extensions.view.getNavigationHolder
import io.scal.ambi.extensions.view.getRouter
import io.scal.ambi.model.interactor.home.chat.ChatListInteractor
import io.scal.ambi.model.interactor.home.chat.IChatListInteractor
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
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
        @Named("localNavigationHolder")
        fun provideLocalNavigation(fragment: ChatListFragment): NavigatorHolder =
            fragment.getNavigationHolder()

        @JvmStatic
        @Provides
        fun provideRouter(fragment: ChatListFragment): Router =
            fragment.getRouter()
    }
}