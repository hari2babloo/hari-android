package io.scal.ambi.model.data.server.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AuthResponse : BaseResponse<String>() {

    @SerializedName("token")
    @Expose
    var token: String? = null

    override fun parse(): String {
        if (null == token) {
            throw IllegalArgumentException("token can not be null")
        }
        return token!!
    }
}