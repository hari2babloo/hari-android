package io.scal.ambi.model.data.server.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.R
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.binding.binders.toFrescoImagePath
import io.scal.ambi.extensions.view.IconImageUser

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

    @SerializedName("kind")
    @Expose
    var kind: Type? = null

    protected fun extractId(): String =
        (id ?: _id) ?: throw IllegalStateException("_id or id can not be null")

    protected fun extractType(): Type =
        (kind ?: type) ?: throw IllegalStateException("type or kind can not be null")

    @Suppress("REDUNDANT_ELSE_IN_WHEN")
    override fun parse(): User =
        when (extractType()) {
            Type.Student -> User.asStudent(extractId(),
                                           profilePicture?.url?.let { IconImageUser(it) } ?: IconImageUser(R.drawable.ic_profile.toFrescoImagePath()),
                                           firstName.orEmpty(),
                                           lastName.orEmpty()
            )
            else         -> throw IllegalStateException("can not parse user from type: ${extractId()}")
        }

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