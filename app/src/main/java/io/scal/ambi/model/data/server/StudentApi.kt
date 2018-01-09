package io.scal.ambi.model.data.server

import io.reactivex.Single
import io.scal.ambi.model.data.server.responses.UserResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface StudentApi {

    @GET("v1/students/{userId}")
    fun getStudentProfile(@Path("userId") userId: String): Single<UserResponse>
}