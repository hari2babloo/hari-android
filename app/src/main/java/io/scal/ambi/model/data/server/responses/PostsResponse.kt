package io.scal.ambi.model.data.server.responses

import io.scal.ambi.entity.feed.NewsFeedItem

class PostsResponse : BaseResponse<List<NewsFeedItem>>() {

    override fun parse(): List<NewsFeedItem> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}