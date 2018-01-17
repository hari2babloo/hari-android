package io.scal.ambi.model.data.server.responses.organization

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.R
import io.scal.ambi.entity.organization.Organization
import io.scal.ambi.entity.organization.OrganizationType
import io.scal.ambi.extensions.notNullOrThrow
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.model.data.server.responses.ItemPicture

class ItemOrganization {

    @SerializedName("_id")
    @Expose
    internal var id: String? = null

    @SerializedName("name")
    @Expose
    internal var name: String? = null

    @SerializedName("slug")
    @Expose
    internal var slug: String? = null

    @SerializedName("description")
    @Expose
    internal var description: String? = null

    @SerializedName("bannerPicture")
    @Expose
    internal var bannerPicture: ItemPicture? = null

    @SerializedName("twilioSids")
    @Expose
    internal var chatUids: List<String>? = null

    fun parse(organizationType: OrganizationType): Organization {
        val parsedId = id.notNullOrThrow("id")
        val parsedName = name.orEmpty()
        val parsedSlug = slug.notNullOrThrow("slug")
        val parsedDescription = description.orEmpty()
        val parsedBannerPicture = bannerPicture?.parse() ?: IconImage(R.drawable.ic_ambi_logo_small)
        val parsedChatUids = chatUids ?: emptyList()

        return when (organizationType) {
            OrganizationType.GROUP -> Organization.Group(parsedId, parsedName, parsedSlug, parsedDescription, parsedBannerPicture, parsedChatUids)
            OrganizationType.CLASS -> Organization.Class(parsedId, parsedName, parsedSlug, parsedDescription, parsedBannerPicture, parsedChatUids)
        }
    }
}