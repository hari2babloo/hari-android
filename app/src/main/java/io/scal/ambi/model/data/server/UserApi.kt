package io.scal.ambi.model.data.server

import io.reactivex.Completable
import io.reactivex.Single
import io.scal.ambi.model.data.server.responses.user.UserResponse
import io.scal.ambi.model.data.server.responses.user.UsersResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {

    @GET("v1/users/{userId}?populate[]=profilePicture&populate[]=bannerPicture")
    fun getUserProfile(@Path("userId") userId: String): Single<UserResponse>

    @GET("v1/users/find/{query}")
    fun searchProfiles(@Path("query") query: String): Single<UsersResponse>

    @PUT("v1/users/{userId}")
    fun updateProfile(@Path("userId") userId: String, @Body updateRequest: UpdateRequest): Completable
}

data class UpdateRequest(val profilePicture: String? = null,
                         val bannerPicture: String? = null)