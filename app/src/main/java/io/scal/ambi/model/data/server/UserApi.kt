package io.scal.ambi.model.data.server

import io.reactivex.Single
import io.scal.ambi.model.data.server.responses.UserResponse
import io.scal.ambi.model.data.server.responses.UsersResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface UserApi {

    @GET("v1/users/{userId}?populate=profilePicture")
    fun getUserProfile(@Path("userId") userId: String): Single<UserResponse>

    @GET("v1/users/find/{query}")
    fun searchProfiles(@Path("query") query: String): Single<UsersResponse>
}