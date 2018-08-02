package io.scal.ambi.model.data.server.responses.organization

import android.graphics.Color
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.ambi.work.R
import io.scal.ambi.entity.organization.Organization
import io.scal.ambi.entity.organization.OrganizationType
import io.scal.ambi.extensions.notNullOrThrow
import io.scal.ambi.extensions.view.ColorIconImage
import io.scal.ambi.model.data.server.responses.ItemPicture
import org.joda.time.DateTime

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

    @SerializedName("color")
    @Expose
    internal var color: String? = null

    @SerializedName("twilioSids")
    @Expose
    internal var chatUids: List<String>? = null



    fun parse(organizationType: OrganizationType): Organization {
        val parsedId = id.notNullOrThrow("id")
        val parsedName = name.orEmpty()
        val parsedSlug = slug.notNullOrThrow("slug")
        val parsedDescription = description.orEmpty()
        var parsedBannerPicture = bannerPicture?.parse()
        if (null == parsedBannerPicture) {
            parsedBannerPicture = ColorIconImage(R.drawable.ic_round_image, color?.let {
                try {
                    Color.parseColor(it)
                } catch (e: Exception) {
                    null
                }
            } ?: 0)
        }
        val parsedChatUids = chatUids ?: emptyList()

        return when (organizationType) {
            OrganizationType.GROUP     -> Organization.Group(parsedId, parsedName, parsedSlug, parsedDescription, parsedBannerPicture, parsedChatUids)
            OrganizationType.CLASS     -> Organization.Class(parsedId, parsedName, parsedSlug, parsedDescription, parsedBannerPicture, parsedChatUids)
            OrganizationType.COMMUNITY -> Organization.Community(parsedId,
                                                                 parsedName,
                                                                 parsedSlug,
                                                                 parsedDescription,
                                                                 parsedBannerPicture,
                                                                 parsedChatUids)
        }
    }
}