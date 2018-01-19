package io.scal.ambi.model.data.server.responses.user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.notNullOrThrow
import io.scal.ambi.model.data.server.responses.BaseResponse

class UsersResponse : BaseResponse<List<User>>() {

    @SerializedName("users")
    @Expose
    internal var users: List<UserResponse.BigUser>? = null

    override fun parse(): List<User> {
        return users.notNullOrThrow("users").map { it.parse() }
    }
}