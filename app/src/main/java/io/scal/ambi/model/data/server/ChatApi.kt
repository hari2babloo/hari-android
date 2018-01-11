package io.scal.ambi.model.data.server

import io.reactivex.Single
import io.scal.ambi.model.data.server.responses.chat.AccessTokenResponse
import retrofit2.http.POST

interface ChatApi {

    @POST("v1/twilio/token/generate")
    fun generateChatAccessToken(): Single<AccessTokenResponse>
}