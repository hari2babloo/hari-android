package io.scal.ambi.model.data.server

import io.reactivex.Single
import io.scal.ambi.model.data.server.responses.organization.ClassResponse
import io.scal.ambi.model.data.server.responses.organization.CommunityResponse
import io.scal.ambi.model.data.server.responses.organization.GroupResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OrganizationApi {

    @GET("v1/groups")
    fun loadGroupOrganizationBySlug(@Query("slug") slug: String): Single<GroupResponse>

    @GET("v1/classes")
    fun loadClassOrganizationBySlug(@Query("slug") slug: String): Single<ClassResponse>

    @GET("v1/communities")
    fun loadCommunityOrganizationBySlug(@Query("slug") slug: String): Single<CommunityResponse>
}