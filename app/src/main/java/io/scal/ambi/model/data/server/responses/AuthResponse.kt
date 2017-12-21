package io.scal.ambi.model.data.server.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.model.repository.auth.AuthResult

class AuthResponse : BaseResponse<AuthResult>() {

    @SerializedName("token")
    @Expose
    var token: String? = null

    override fun parse(): AuthResult {
        if (null == token) {
            throw IllegalArgumentException("token can not be null")
        }
        return AuthResult(token!!)
    }
}