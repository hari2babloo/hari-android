package io.scal.ambi.model.repository.data.newsfeed

import io.reactivex.Single
import io.scal.ambi.entity.feed.NewsFeedItem

interface IPostsRepository {

    fun loadPostsGeneral(lastPostTime: Long): Single<List<NewsFeedItem>>
}