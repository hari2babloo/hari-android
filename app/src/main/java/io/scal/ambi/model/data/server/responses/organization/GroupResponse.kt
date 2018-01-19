package io.scal.ambi.model.data.server.responses.organization

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.entity.organization.Organization
import io.scal.ambi.entity.organization.OrganizationType
import io.scal.ambi.extensions.notNullOrThrow
import io.scal.ambi.model.data.server.responses.BaseResponse

class GroupResponse : BaseResponse<Organization>() {

    @SerializedName("group")
    @Expose
    internal var groupOrg: ItemOrganization? = null

    override fun parse(): Organization {
        return groupOrg.notNullOrThrow("group").parse(OrganizationType.GROUP)
    }
}