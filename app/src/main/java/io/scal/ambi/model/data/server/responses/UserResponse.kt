package io.scal.ambi.model.data.server.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.R
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.binding.binders.toFrescoImagePath
import io.scal.ambi.extensions.notNullOrThrow
import io.scal.ambi.extensions.view.IconImageUser

class UserResponse : BaseResponse<User>() {

    @SerializedName("user")
    @Expose
    internal var user: BigUser? = null

    override fun parse(): User {
        user.notNullOrThrow("user").type = ItemUser.Type.Student // todo remove this
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