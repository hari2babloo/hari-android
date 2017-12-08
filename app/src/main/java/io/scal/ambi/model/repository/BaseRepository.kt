package io.scal.ambi.model.repository

import io.scal.ambi.model.data.server.ServerResponseException
import org.json.JSONObject
import retrofit2.HttpException

fun Throwable.toServerResponseException(): Throwable =
    when {
        this is ServerResponseException -> this
        this is HttpException           -> {
            var message = ""
            try {
                val messageBody = response().errorBody()?.string()
                message = JSONObject(messageBody).getString("message")
            } catch (inner: Throwable) {
                // pass
            }
            ServerResponseException(this, code(), message)
        }
        else                            -> this
    }