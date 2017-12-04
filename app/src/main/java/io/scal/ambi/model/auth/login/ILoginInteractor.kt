package io.scal.ambi.model.auth.login

import io.reactivex.Completable

interface ILoginInteractor {

    fun login(userName: String, password: String): Completable
}