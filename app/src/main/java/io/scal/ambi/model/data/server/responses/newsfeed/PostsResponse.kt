package io.scal.ambi.model.data.server.responses.newsfeed

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.entity.feed.NewsFeedItem
import io.scal.ambi.extensions.notNullOrThrow
import io.scal.ambi.model.data.server.responses.BaseResponse

class PostsResponse : BaseResponse<List<NewsFeedItem>>() {

    @SerializedName("post")
    @Expose
    internal var posts1: List<ItemPost>? = null

    @SerializedName("posts")
    @Expose
    internal var posts2: List<ItemPost>? = null

    private val posts: List<ItemPost>?
        get() = posts1 ?: posts2

    override fun parse(): List<NewsFeedItem> {
        return posts.notNullOrThrow("posts").mapNotNull { it.parse() }
    }
}