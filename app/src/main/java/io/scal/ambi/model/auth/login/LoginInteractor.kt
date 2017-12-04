package io.scal.ambi.model.auth.login

import io.reactivex.Completable
import javax.inject.Inject

class LoginInteractor @Inject constructor() : ILoginInteractor {

    override fun login(userName: String, password: String): Completable {
        return Completable.error(NotImplementedError())
    }
}