package io.scal.ambi.ui.home.classes.about

import com.ambi.work.R
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.entity.user.UserType
import io.scal.ambi.extensions.binding.binders.toFrescoImagePath
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.IconImageUser
import io.scal.ambi.model.data.server.responses.Parceble
import io.scal.ambi.model.data.server.responses.user.ItemUser

/**
 * Created by chandra on 04-08-2018.
 */
internal class ItemMembers : Parceble<MembersData?> {

    @SerializedName("_id")
    @Expose
    internal var id: String? = null

    @SerializedName("firstName")
    @Expose
    internal var firstName: String? = null

    @SerializedName("kind")
    @Expose
    var kind: String? = null

    override fun parse(): MembersData? {
        return MembersData(id, firstName, IconImageUser(R.drawable.ic_profile.toFrescoImagePath()), kind!!)
    }

}

open class MembersData(val id: String?, val name: String?, val icon: IconImage, val kind: String){

    var userType = kind.toLowerCase()

    var colorId = when(kind){
        ItemUser.Type.Student.name -> UserType.STUDENT.colorId
        ItemUser.Type.Faculty.name -> UserType.FACULTY.colorId
        else -> UserType.UNKNOWN.colorId
    }
}

data class Header(val _id: String, val _name: String?,val _icon: IconImage, val _kind: String)
data class HeaderSecondary(val _id: String, val _name: String?,val _icon: IconImage, val _kind: String, val count: Int)
data class MemberCount(val count: Int)