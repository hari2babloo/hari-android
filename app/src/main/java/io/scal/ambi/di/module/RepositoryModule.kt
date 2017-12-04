package io.scal.ambi.di.module

import dagger.Binds
import dagger.Module
import io.scal.ambi.model.repository.auth.AuthRepository
import io.scal.ambi.model.repository.auth.IAuthRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import io.scal.ambi.model.repository.local.LocalUserDataRepository

@Module
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthRepository(authRepository: AuthRepository): IAuthRepository

    @Binds
    abstract fun bindLocalRepository(localUserDataRepository: LocalUserDataRepository): ILocalUserDataRepository
}