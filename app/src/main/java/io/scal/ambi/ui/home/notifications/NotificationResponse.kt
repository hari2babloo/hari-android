package io.scal.ambi.ui.home.notifications

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.extensions.notNullOrThrow
import io.scal.ambi.model.data.server.responses.BaseResponse

class NotificationResponse : BaseResponse<List<NotificationData>>() {

    @SerializedName("notifications")
    @Expose
    internal var posts1: List<ItemNotification>? = null


    private val posts: List<ItemNotification>?
        get() = posts1

    override fun parse(): List<NotificationData> {
        return posts.notNullOrThrow("notifications").mapNotNull { it.parse() }
    }
}