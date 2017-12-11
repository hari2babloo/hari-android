package io.scal.ambi.model.repository.data.student

import io.reactivex.Single
import io.scal.ambi.entity.User
import io.scal.ambi.model.data.server.StudentApi
import io.scal.ambi.model.repository.toServerResponseException
import javax.inject.Inject

class StudentRepository @Inject constructor(private val studentApi: StudentApi) : IStudentRepository {

    override fun getStudentProfile(userId: String): Single<User> {
        return studentApi.getStudentProfile(userId)
            .map { it.parse() }
            .onErrorResumeNext { t -> Single.error(t.toServerResponseException()) }
    }
}