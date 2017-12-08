package io.scal.ambi.model.repository.data.student

import io.reactivex.Single
import io.scal.ambi.entity.User
import io.scal.ambi.extensions.view.IconImageUser
import io.scal.ambi.model.data.server.StudentApi
import io.scal.ambi.model.repository.toServerResponseException
import java.security.SecureRandom
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class StudentRepository @Inject constructor(private val studentApi: StudentApi) : IStudentRepository {

    override fun getCurrentStudentProfile(token: String): Single<User> {
        // todo change to real request
        return Single.just(
            if (SecureRandom().nextBoolean()) {
                User()
            } else {
                User("0", IconImageUser("https://cdn.pixabay.com/photo/2015/03/01/14/39/thing-654750_960_720.png"))
            }
        )
            .delay(15, TimeUnit.SECONDS)
            .onErrorResumeNext { t -> Single.error(t.toServerResponseException()) }
    }
}