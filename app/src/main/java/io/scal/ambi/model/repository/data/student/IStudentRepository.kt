package io.scal.ambi.model.repository.data.student

import io.reactivex.Single
import io.scal.ambi.entity.User

interface IStudentRepository {

    fun getStudentProfile(userId: String): Single<User>
}