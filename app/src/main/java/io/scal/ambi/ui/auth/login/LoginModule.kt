package io.scal.ambi.ui.auth.login

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.model.interactor.auth.login.ILoginInteractor
import io.scal.ambi.model.interactor.auth.login.LoginInteractor
import io.scal.ambi.presentation.auth.LoginViewModel

@Module
abstract class LoginModule {

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindUsersViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    abstract fun bindLauncherInteractor(loginInteractor: LoginInteractor): ILoginInteractor
}