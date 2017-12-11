package io.scal.ambi.model.repository.auth

import io.reactivex.Completable
import io.reactivex.Single
import io.scal.ambi.model.data.server.AuthApi
import io.scal.ambi.model.data.server.LoginRequest
import io.scal.ambi.model.repository.toServerResponseException
import javax.inject.Inject

class AuthRepository @Inject constructor(private val authApi: AuthApi) : IAuthRepository {

    override fun login(email: String, password: String): Single<AuthResult> =
        authApi.login(LoginRequest(email, password))
            .map { it.parse() }
            .onErrorResumeNext { t -> Single.error(t.toServerResponseException()) }

    override fun recover(email: String): Completable =
        authApi.recover()
}