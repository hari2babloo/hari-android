package io.scal.ambi.model.data.server

import io.reactivex.Single
import io.scal.ambi.model.data.server.responses.StudentResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface StudentApi {

    @GET("students/{userId}")
    fun getStudentProfile(@Path("userId") userId: String): Single<StudentResponse>
}