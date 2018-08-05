package io.scal.ambi.ui.home.classes.about

import com.ambi.work.R
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.extensions.binding.binders.toFrescoImagePath
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.IconImageUser
import io.scal.ambi.model.data.server.responses.Parceble

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

    override fun parse(): MembersData? {
        return MembersData(firstName, IconImageUser(R.drawable.ic_profile.toFrescoImagePath()))
    }

}

data class MembersData(val name: String?,val icon: IconImage)