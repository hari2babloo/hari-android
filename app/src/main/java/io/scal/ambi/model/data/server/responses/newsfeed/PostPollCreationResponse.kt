package io.scal.ambi.model.data.server.responses.newsfeed

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.entity.feed.NewsFeedItem
import io.scal.ambi.extensions.notNullOrThrow
import io.scal.ambi.model.data.server.responses.BaseResponse

class PostPollCreationResponse : BaseResponse<NewsFeedItem>() {

    @SerializedName("pollPost")
    @Expose
    internal var postItem: ItemPost? = null

    override fun parse(): NewsFeedItem {
        return postItem.notNullOrThrow("postItem").parse()!!
    }
}