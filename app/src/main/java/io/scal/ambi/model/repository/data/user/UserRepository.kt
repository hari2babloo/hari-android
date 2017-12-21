package io.scal.ambi.model.repository.data.user

import com.google.gson.Gson
import io.reactivex.Single
import io.scal.ambi.entity.user.User
import io.scal.ambi.model.data.server.StudentApi
import io.scal.ambi.model.data.server.responses.ItemUser
import io.scal.ambi.model.data.server.responses.UserResponse
import io.scal.ambi.model.repository.toServerResponseException
import javax.inject.Inject

class UserRepository @Inject constructor(private val studentApi: StudentApi) : IUserRepository {

    private val gson = Gson()

    override fun extractUserProfileFromData(userJson: String): Single<User> {
        return Single
            .fromCallable { userJson.createUser(gson) }
            .map { it.parse() }
    }

    override fun getProfile(userId: String): Single<User> {
        return studentApi.getStudentProfile(userId)
            .map { it.parse() }
            .onErrorResumeNext { t -> Single.error(t.toServerResponseException()) }
    }
}

private fun String.createUser(gson: Gson): ItemUser {
    val generalUser = gson.fromJson(this, ItemUser::class.java)
    return when (generalUser.type) {
        null                  -> throw IllegalArgumentException("can not parse user ($this) because there is no type")
        ItemUser.Type.Student -> gson.fromJson(this, UserResponse.Student::class.java)
    }
}