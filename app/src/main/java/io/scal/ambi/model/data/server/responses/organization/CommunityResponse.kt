package io.scal.ambi.model.data.server.responses.organization

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.entity.organization.Organization
import io.scal.ambi.entity.organization.OrganizationType
import io.scal.ambi.extensions.notNullOrThrow
import io.scal.ambi.model.data.server.responses.BaseResponse

class CommunityResponse : BaseResponse<Organization>() {

    @SerializedName("community")
    @Expose
    internal var communityOrg: ItemOrganization? = null

    override fun parse(): Organization {
        return communityOrg.notNullOrThrow("community").parse(OrganizationType.COMMUNITY)
    }
}