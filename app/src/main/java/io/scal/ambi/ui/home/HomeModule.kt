package io.scal.ambi.ui.home

import android.app.Activity
import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.presentation.home.root.HomeViewModel

@Module
abstract class HomeModule {

    @Binds
    abstract fun bindActivity(activity: HomeActivity): Activity

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindViewModel(viewModel: HomeViewModel): ViewModel
}