package io.scal.ambi.model.repository.auth

import io.reactivex.Completable
import io.reactivex.Single
import io.scal.ambi.model.data.server.AuthApi
import javax.inject.Inject

class AuthRepository @Inject constructor(private val authApi: AuthApi) : IAuthRepository {

    override fun login(userName: String, password: String): Single<AuthResult> =
            authApi.login()
                    .map { it.parse() }

    override fun recover(email: String): Completable =
            authApi.recover()
}