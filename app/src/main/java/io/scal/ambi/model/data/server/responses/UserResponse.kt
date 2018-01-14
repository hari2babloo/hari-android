package io.scal.ambi.model.data.server.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.entity.user.User

class UserResponse : BaseResponse<User>() {

    @SerializedName("user")
    @Expose
    internal var user: BigUser? = null

    override fun parse(): User {
        return user!!.parse()
    }

    class BigUser : ItemUser() {

        @SerializedName("school")
        @Expose
        var schoolId: String? = null

        @SerializedName("gender")
        @Expose
        var gender: String? = null

    }
}