package io.scal.ambi.model.data.server.responses.chat

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.extensions.notNullOrThrow
import io.scal.ambi.model.data.server.responses.Parceble
import io.scal.ambi.model.repository.data.chat.data.AccessInfo

class AccessTokenResponse : Parceble<AccessInfo> {

    @SerializedName("identity")
    @Expose
    var identity: String? = null

    @SerializedName("token")
    @Expose
    var token: String? = null

    override fun parse(): AccessInfo {
        return AccessInfo.Data(token.notNullOrThrow("token"), identity.notNullOrThrow("identity"))
    }
}