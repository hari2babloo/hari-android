package io.scal.ambi.model.repository.data.organization

import io.reactivex.Single
import io.scal.ambi.entity.organization.Organization
import io.scal.ambi.model.repository.data.chat.data.ChatChannelInfo

interface IOrganizationRepository {

    fun loadOrganizationBySlug(organizationSmall: ChatChannelInfo.OrganizationSmall): Single<Organization>
}