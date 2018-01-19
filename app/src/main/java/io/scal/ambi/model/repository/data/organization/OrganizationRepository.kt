package io.scal.ambi.model.repository.data.organization

import io.reactivex.Single
import io.scal.ambi.entity.organization.Organization
import io.scal.ambi.entity.organization.OrganizationType
import io.scal.ambi.model.data.server.OrganizationApi
import io.scal.ambi.model.repository.data.chat.data.ChatChannelInfo
import javax.inject.Inject

class OrganizationRepository @Inject constructor(private val organizationApi: OrganizationApi) : IOrganizationRepository {

    override fun loadOrganizationBySlug(organizationSmall: ChatChannelInfo.OrganizationSmall): Single<Organization> {
        return when (organizationSmall.type) {
            OrganizationType.GROUP     -> organizationApi.loadGroupOrganizationBySlug(organizationSmall.slug)
            OrganizationType.CLASS     -> organizationApi.loadClassOrganizationBySlug(organizationSmall.slug)
            OrganizationType.COMMUNITY -> organizationApi.loadCommunityOrganizationBySlug(organizationSmall.slug)
        }
            .map { it.parse() }
    }
}