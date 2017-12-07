package io.scal.ambi.model.repository.local

import io.reactivex.Completable
import io.reactivex.Observable
import io.scal.ambi.model.repository.auth.AuthResult

interface ILocalUserDataRepository {

    fun saveUserInfo(authResult: AuthResult): Completable

    fun removeUserInfo(): Completable

    fun observeUserInfo(): Observable<AuthResult>

    fun getUserInfo(): AuthResult?
}