package io.scal.ambi.model.repository.data.user

import com.google.gson.Gson
import io.reactivex.Single
import io.scal.ambi.entity.user.User
import io.scal.ambi.entity.user.UserResume
import io.scal.ambi.model.data.server.UpdateRequest
import io.scal.ambi.model.data.server.UserApi
import io.scal.ambi.model.data.server.responses.user.ItemUser
import io.scal.ambi.model.data.server.responses.user.UserResponse
import io.scal.ambi.model.repository.local.ILocalDataRepository
import io.scal.ambi.model.repository.toServerResponseException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class UserRepository @Inject constructor(private val userApi: UserApi,
                                         private val localDataRepository: ILocalDataRepository) : IUserRepository {

    private val gson = Gson()
    private val maxProfileLifeTime: Long = TimeUnit.MINUTES.toMillis(1)

    override fun extractUserProfileFromData(userJson: String): Single<User> {
        return Single
            .fromCallable { userJson.createUser(gson) }
            .map { it.parse() }
    }

    override fun getProfile(userId: String): Single<User> {
        return userApi.getUserProfile(userId)
            .map { it.parse() }
            .onErrorResumeNext { t -> Single.error(t.toServerResponseException()) }
            .doOnSuccess { localDataRepository.putUserProfile(it) }
    }

    override fun getProfileCached(userId: String): Single<User> {
        return localDataRepository
            .getUserProfile(userId, maxProfileLifeTime)
            .onErrorResumeNext(getProfile(userId))
    }

    override fun searchProfiles(searchQuery: String): Single<List<User>> {
        return userApi.searchProfiles(searchQuery)
            .map { it.parse() }
    }

    override fun saveProfileAvatar(userId: String, fileId: String): Single<User> {
        return userApi.updateProfile(userId, UpdateRequest(profilePicture = fileId))
            .andThen(getProfile(userId))
    }

    override fun saveProfileBanner(userId: String, fileId: String): Single<User> {
        return userApi.updateProfile(userId, UpdateRequest(bannerPicture = fileId))
            .andThen(getProfile(userId))
    }

    override fun loadUserResume(userUid: String): Single<UserResume> {
        return userApi.getUserProfile(userUid)
            .map { it.parseAsResume() }
    }
}

private fun String.createUser(gson: Gson): ItemUser {
    val generalUser = gson.fromJson(this, ItemUser::class.java)
    return when (generalUser.type) {
        null                  -> throw IllegalArgumentException("can not parse user ($this) because there is no type")
        ItemUser.Type.Student -> gson.fromJson(this, UserResponse.BigUser::class.java)
    }
}