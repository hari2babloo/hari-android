package io.scal.ambi.model.repository.local

import io.reactivex.Single
import io.scal.ambi.entity.user.User

interface ILocalDataRepository {

    fun getDeviceUid(): String

    fun getUserProfile(userUid: String, maxProfileLifeTimeMillis: Long): Single<User>

    fun putUserProfile(user: User)
}