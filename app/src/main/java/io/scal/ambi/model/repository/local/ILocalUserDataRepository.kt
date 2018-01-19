package io.scal.ambi.model.repository.local

import io.reactivex.Completable
import io.reactivex.Observable
import io.scal.ambi.entity.user.User

interface ILocalUserDataRepository {

    fun saveCurrentUser(user: User): Completable

    fun observeCurrentUser(): Observable<User>

    fun getCurrentUser(): User?

    fun saveCurrentToken(token: String): Completable

    fun getCurrentToken(): String?

    fun removeAllUserInfo(): Completable
}