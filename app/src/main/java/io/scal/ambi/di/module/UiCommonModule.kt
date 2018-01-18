package io.scal.ambi.di.module

import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import io.scal.ambi.model.interactor.auth.profile.AuthProfileCheckerInteractor
import io.scal.ambi.model.interactor.auth.profile.IAuthProfileCheckerInteractor
import io.scal.ambi.model.interactor.base.AmazonFileUploadInteractor
import io.scal.ambi.model.interactor.base.IFileUploadInteractor
import javax.inject.Singleton

@Module
abstract class UiCommonModule {

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory


    @Singleton
    @Binds
    abstract fun bindAuthInteractor(interactor: AuthProfileCheckerInteractor): IAuthProfileCheckerInteractor

    @Singleton
    @Binds
    abstract fun bindFileUploadInteractor(interactor: AmazonFileUploadInteractor): IFileUploadInteractor

}