package io.scal.ambi.ui.auth.profile

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey

@Module
abstract class AuthProfileModule {

    @Binds
    @IntoMap
    @ViewModelKey(AuthProfileCheckerViewModel::class)
    abstract fun bindViewModel(viewModel: AuthProfileCheckerViewModel): ViewModel
}