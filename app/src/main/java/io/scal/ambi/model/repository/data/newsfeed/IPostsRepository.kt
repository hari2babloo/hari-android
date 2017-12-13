package io.scal.ambi.model.repository.data.newsfeed

import io.reactivex.Completable
import io.reactivex.Single
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.entity.feed.NewsFeedItem
import org.joda.time.Duration

interface IPostsRepository {

    fun loadPostsGeneral(lastPostTime: Long?): Single<List<NewsFeedItem>>

    fun postNewPoll(pinned: Boolean,
                    locked: Boolean,
                    asUserUid: String,
                    questionText: String,
                    choices: List<String>,
                    duration: Duration?,
                    audience: List<Audience>,
                    hosts: List<Host>): Completable

    data class Host(val id: String, val kind: PostHostKind)
}