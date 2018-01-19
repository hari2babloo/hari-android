package io.scal.ambi.model.data.server.responses.newsfeed

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.entity.feed.NewsFeedItem
import io.scal.ambi.model.data.server.responses.BaseResponse

class PostCreationResponse : BaseResponse<NewsFeedItem>() {

    @SerializedName("pollPost")
    @Expose
    internal var pollPost: ItemPost? = null

    @SerializedName("updatePost")
    @Expose
    internal var updatePost: ItemPost? = null

    @SerializedName("announcementPost")
    @Expose
    internal var announcementPost: ItemPost? = null

    override fun parse(): NewsFeedItem =
        when {
            null != pollPost         -> pollPost!!.parse()!!
            null != updatePost       -> updatePost!!.parse()!!
            null != announcementPost -> announcementPost!!.parse()!!
            else                     -> throw IllegalArgumentException("strange thing. unknown post item: $this")
        }
}