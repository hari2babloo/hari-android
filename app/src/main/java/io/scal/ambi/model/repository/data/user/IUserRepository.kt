package io.scal.ambi.model.repository.data.user

import io.reactivex.Single
import io.scal.ambi.entity.user.User
import io.scal.ambi.entity.user.UserResume

interface IUserRepository {

    fun extractUserProfileFromData(userJson: String): Single<User>

    fun getProfile(userId: String): Single<User>

    fun getProfileCached(userId: String): Single<User>

    fun searchProfiles(searchQuery: String): Single<List<User>>

    fun saveProfileAvatar(userId: String, fileId: String): Single<User>

    fun saveProfileBanner(userId: String, fileId: String): Single<User>

    fun loadUserResume(userUid: String): Single<UserResume>
}