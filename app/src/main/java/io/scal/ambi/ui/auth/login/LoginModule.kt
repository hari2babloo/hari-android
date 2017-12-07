package io.scal.ambi.ui.auth.login

import android.app.Activity
import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.model.interactor.auth.login.ILoginInteractor
import io.scal.ambi.model.interactor.auth.login.LoginInteractor

@Module
internal abstract class LoginModule {

    @Binds
    abstract fun bindActivity(activity: LoginActivity): Activity

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    abstract fun bindInteractor(loginInteractor: LoginInteractor): ILoginInteractor
}