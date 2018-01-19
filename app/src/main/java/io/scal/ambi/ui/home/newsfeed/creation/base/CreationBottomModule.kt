package io.scal.ambi.ui.home.newsfeed.creation.base

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey

@Module
abstract class CreationBottomModule {

    @Binds
    @IntoMap
    @ViewModelKey(CreationBottomViewModel::class)
    abstract fun bindViewModel(viewModel: CreationBottomViewModel): ViewModel
}