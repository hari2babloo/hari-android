package io.scal.ambi.model.data.server.responses.user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.ambi.work.R
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.binding.binders.toFrescoImagePath
import io.scal.ambi.extensions.view.IconImageUser
import io.scal.ambi.model.data.server.responses.ItemPicture
import io.scal.ambi.model.data.server.responses.Parceble
import timber.log.Timber

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

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("profilePicture")
    @Expose
    var profilePicture: ItemPicture? = null

    @SerializedName("type")
    @Expose
    var type: Type? = null

    @SerializedName("kind")
    @Expose
    var kind: Type? = null

    protected fun extractId(): String =
        (id ?: _id) ?: throw IllegalStateException("_id or id can not be null")

    protected fun extractType(): Type? =
        (kind ?: type) /*?: throw IllegalStateException("type or kind can not be null")*/ // todo remove comment

    @Suppress("REDUNDANT_ELSE_IN_WHEN")
    override fun parse(): User {
        val avatar = profilePicture?.parse()?.let { IconImageUser(it.iconPath) } ?: IconImageUser(R.drawable.ic_profile.toFrescoImagePath())
        return when (extractType()) {
            Type.Student -> User.asStudent(extractId(), avatar, firstName.orEmpty(), lastName.orEmpty()
            )
            else         -> {
                Timber.w("empty user type field! now skip to unknown data")
                User.asSimple(extractId(), avatar, firstName.orEmpty(), lastName.orEmpty())
            }
        }
    }

    enum class Type {
        Student
    }
}