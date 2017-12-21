package io.scal.ambi.di.module

import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import io.scal.ambi.model.interactor.auth.profile.AuthProfileCheckerInteractor
import io.scal.ambi.model.interactor.auth.profile.IAuthProfileCheckerInteractor
import javax.inject.Singleton

@Module
abstract class UiCommonModule {

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory


    @Singleton
    @Binds
    abstract fun bindInteractor(interactor: AuthProfileCheckerInteractor): IAuthProfileCheckerInteractor
}