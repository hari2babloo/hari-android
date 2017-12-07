package io.scal.ambi.model.data.server.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

abstract class BaseResponse<out T> : Parceble<T> {

    @SerializedName("message")
    @Expose
    var message: String? = null
}