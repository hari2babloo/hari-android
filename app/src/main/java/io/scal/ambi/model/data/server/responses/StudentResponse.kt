package io.scal.ambi.model.data.server.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.entity.User

class StudentResponse : BaseResponse<User>() {

    @SerializedName("student")
    @Expose
    internal var student: Student? = null

    override fun parse(): User {
        return student!!.parse()
    }

    internal inner class Student : ItemUser() {

        @SerializedName("birthDay")
        @Expose
        var birthDay: String? = null

        @SerializedName("school")
        @Expose
        var schoolId: String? = null

        @SerializedName("gender")
        @Expose
        var gender: String? = null
    }
}