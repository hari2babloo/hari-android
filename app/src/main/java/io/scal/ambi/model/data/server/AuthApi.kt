package io.scal.ambi.model.data.server

import io.reactivex.Completable
import io.reactivex.Single
import io.scal.ambi.model.data.server.responses.AuthResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("v1/auth")
    fun login(@Body loginRequest: LoginRequest): Single<AuthResponse>

    @POST("v1/recover")
    fun recover(): Completable
}

class LoginRequest(val email: String, val password: String)