package io.scal.ambi.model.interactor.auth.profile

import io.reactivex.Observable
import io.scal.ambi.entity.User
import io.scal.ambi.extensions.view.IconImageUser
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AuthProfileCheckerInteractor @Inject constructor() : IAuthProfileCheckerInteractor {

    override fun getUserProfile(): Observable<User> {
        val first = Observable.just(User())
        val second = Observable.just(User("0", IconImageUser("https://cdn.pixabay.com/photo/2015/03/01/14/39/thing-654750_960_720.png"))).delay(10,
                                                                                                                                                TimeUnit.SECONDS)
        return Observable.concat(first, second)
    }
}