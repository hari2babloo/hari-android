package io.scal.ambi.model.data.server.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.entity.user.User

open class ItemUser : Parceble<User> {

    @SerializedName("_id")
    @Expose
    var _id: String? = null

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("firstName")
    @Expose
    var firstName: String? = null

    @SerializedName("lastName")
    @Expose
    var lastName: String? = null

    @SerializedName("profilePicture")
    @Expose
    var profilePicture: Picture? = null

    @SerializedName("type")
    @Expose
    var type: Type? = null

    protected fun extractId(): String =
        (id ?: _id) ?: throw IllegalStateException("_id or id can not be null")

    override fun parse(): User = throw UnsupportedOperationException("can not parse raw user")

    enum class Type {
        Student
    }

    class Picture {

        @SerializedName("_id")
        @Expose
        var _id: String? = null

        @SerializedName("url")
        @Expose
        var url: String? = null
    }
}