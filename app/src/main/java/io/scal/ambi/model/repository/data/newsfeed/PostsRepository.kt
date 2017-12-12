package io.scal.ambi.model.repository.data.newsfeed

import io.reactivex.Completable
import io.reactivex.Single
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.entity.feed.NewsFeedItem
import io.scal.ambi.model.data.server.ChoiceRequest
import io.scal.ambi.model.data.server.PollCreationRequest
import io.scal.ambi.model.data.server.PostsApi
import org.joda.time.Duration
import javax.inject.Inject

class PostsRepository @Inject constructor(private val postsApi: PostsApi) : IPostsRepository {

    override fun loadPostsGeneral(lastPostTime: Long?): Single<List<NewsFeedItem>> {
        return postsApi.getPostsGeneral(lastPostTime)
            .map { it.parse() }
    }

    override fun postNewPoll(pinned: Boolean,
                             locked: Boolean,
                             asUserUid: String,
                             questionText: String,
                             choices: List<String>,
                             duration: Duration?,
                             audience: List<Audience>): Completable {
        return postsApi
            .postNewPoll(PollCreationRequest(asUserUid,
                                             pinned,
                                             locked,
                                             questionText,
                                             audience.map { it.toServerName() },
                                             choices.map { ChoiceRequest(it) },
                                             duration?.millis)
            )
            .toCompletable()
    }
}

private fun Audience.toServerName(): String =
    when (this) {
        Audience.STUDENTS -> "Student"
        Audience.FACULTY  -> "Faculty"
        Audience.STAFF    -> "Staff"
        else              -> throw IllegalArgumentException("$this is not listed in server requests names")
    }
