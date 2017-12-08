package io.scal.ambi.model.repository.data.newsfeed

import io.reactivex.Single
import io.scal.ambi.entity.feed.NewsFeedItem
import io.scal.ambi.model.data.server.PostsApi
import javax.inject.Inject

class PostsRepository @Inject constructor(private val postsApi: PostsApi) : IPostsRepository {

    override fun loadPostsGeneral(lastPostTime: Long?): Single<List<NewsFeedItem>> {
        return postsApi.getPostsGeneral(lastPostTime)
            .map { it.parse() }
    }
}