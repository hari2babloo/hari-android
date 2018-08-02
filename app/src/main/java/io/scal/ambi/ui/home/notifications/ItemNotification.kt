package io.scal.ambi.ui.home.notifications
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.model.data.server.responses.Parceble
import io.scal.ambi.model.data.server.responses.organization.ItemOrganization
import io.scal.ambi.model.data.server.responses.user.ItemUser

internal class ItemNotification : Parceble<NotificationData?> {

    @SerializedName("_id")
    @Expose
    internal var id: String? = null

    @SerializedName("type")
    @Expose
    internal var type: String? = null

    @SerializedName("from")
    @Expose
    internal var from: ItemUser? = null

    @SerializedName("fromOrganization")
    @Expose
    internal var fromOrganization: ItemOrganization? = null

    @SerializedName("timeOfEvent")
    @Expose
    var timeOfEvent: String? = null

    @SerializedName("createdAt")
    @Expose
    var createdAt: String? = null

    @SerializedName("category")
    @Expose
    var category: String? = null

    @SerializedName("isRead")
    @Expose
    var isRead: Boolean? = null

    override fun parse(): NotificationData? {
        return parseAsAnnouncement()
    }

    private fun parseAsAnnouncement(): NotificationData {
        return NotificationData(type,from,fromOrganization,timeOfEvent,createdAt,category,isRead)
    }

}
