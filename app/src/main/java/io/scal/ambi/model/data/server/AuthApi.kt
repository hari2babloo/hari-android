package io.scal.ambi.model.data.server

import io.reactivex.Completable
import io.reactivex.Single
import io.scal.ambi.model.data.server.responses.AuthResponse
import retrofit2.http.POST

interface AuthApi {

    @POST("login")
    fun login(): Single<AuthResponse>

    @POST("recover")
    fun recover(): Completable
}