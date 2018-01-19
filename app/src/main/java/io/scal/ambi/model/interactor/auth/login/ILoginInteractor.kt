package io.scal.ambi.model.interactor.auth.login

import io.reactivex.Completable

interface ILoginInteractor {

    fun login(email: String, password: String): Completable
}