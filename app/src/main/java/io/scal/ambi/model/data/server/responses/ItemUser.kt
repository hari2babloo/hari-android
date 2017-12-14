package io.scal.ambi.model.data.server.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.R
import io.scal.ambi.entity.User
import io.scal.ambi.extensions.binding.binders.toFrescoImagePath
import io.scal.ambi.extensions.view.IconImageUser

open class ItemUser : Parceble<User> {

    @SerializedName("_id")
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
    var profilePicture: String? = null

    override fun parse(): User {
        return User(id!!,
                    profilePicture?.let { IconImageUser(it) } ?: IconImageUser(R.drawable.ic_profile.toFrescoImagePath()),
                    firstName!!,
                    lastName!!
        )
    }
}
