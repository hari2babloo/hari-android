package io.scal.ambi.model.data.server.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.R
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.binding.binders.toFrescoImagePath
import io.scal.ambi.extensions.notNullOrThrow
import io.scal.ambi.extensions.view.IconImageUser

class UserResponse : BaseResponse<User>() {

    @SerializedName("student")
    @Expose
    internal var student: Student? = null

    override fun parse(): User {
        student.notNullOrThrow("student").type = ItemUser.Type.Student
        return student!!.parse()
    }

    class Student : ItemUser() {

        @SerializedName("birthDay")
        @Expose
        var birthDay: String? = null

        @SerializedName("school")
        @Expose
        var schoolId: String? = null

        @SerializedName("gender")
        @Expose
        var gender: String? = null

        override fun parse(): User {
            if (Type.Student != type) {
                throw IllegalStateException("can not parse Student from type: $type")
            }
            return User.asStudent(extractId(),
                                  profilePicture?.url?.let { IconImageUser(it) } ?: IconImageUser(R.drawable.ic_profile.toFrescoImagePath()),
                                  firstName.orEmpty(),
                                  lastName.orEmpty()
            )
        }
    }
}