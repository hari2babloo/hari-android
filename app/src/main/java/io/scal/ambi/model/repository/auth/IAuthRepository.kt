package io.scal.ambi.model.repository.auth

import io.reactivex.Completable
import io.reactivex.Single

interface IAuthRepository {

    fun login(email: String, password: String): Single<AuthResult>

    fun recover(email: String): Completable
}