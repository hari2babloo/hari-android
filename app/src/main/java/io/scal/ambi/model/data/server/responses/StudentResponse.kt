package io.scal.ambi.model.data.server.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.R
import io.scal.ambi.entity.User
import io.scal.ambi.extensions.binding.binders.toFrescoImagePath
import io.scal.ambi.extensions.view.IconImageUser

class StudentResponse : BaseResponse<User>() {

    @SerializedName("student")
    @Expose
    internal var student: Student? = null

    override fun parse(): User {
        val studentObj = student!!

        return User(studentObj.id!!,
                    studentObj.profilePicture?.let { IconImageUser(it) } ?: IconImageUser(R.drawable.ic_profile.toFrescoImagePath()),
                    studentObj.firstName!!,
                    studentObj.lastName!!
        )
    }

    internal inner class Student {

        @SerializedName("_id")
        @Expose
        var id: String? = null

        @SerializedName("firstName")
        @Expose
        var firstName: String? = null

        @SerializedName("lastName")
        @Expose
        var lastName: String? = null

        @SerializedName("birthDay")
        @Expose
        var birthDay: String? = null

        @SerializedName("school")
        @Expose
        var schoolId: String? = null

        @SerializedName("gender")
        @Expose
        var gender: String? = null

        @SerializedName("profilePicture")
        @Expose
        var profilePicture: String? = null
    }
}