package io.scal.ambi.model.data.server

import io.reactivex.Single
import io.scal.ambi.model.data.server.responses.chat.AccessTokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatApi {

    @POST("v1/twilio/token/generate")
    fun generateChatAccessToken(@Body request: AccessTokenRequest): Single<AccessTokenResponse>

    class AccessTokenRequest(val identity: String, val deviceId: String, val device: String = "android")
}