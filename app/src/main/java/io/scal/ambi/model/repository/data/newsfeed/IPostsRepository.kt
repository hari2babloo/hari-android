package io.scal.ambi.model.repository.data.newsfeed

import io.reactivex.Completable
import io.reactivex.Single
import io.scal.ambi.entity.feed.AnnouncementType
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.entity.feed.NewsFeedItem
import org.joda.time.Duration

interface IPostsRepository {

    fun loadPostsGeneral(lastPostTime: Long?): Single<List<NewsFeedItem>>

    fun postNewStatus(pinned: Boolean,
                      locked: Boolean,
                      asUserUid: String,
                      statusText: String,
                      audiences: List<Audience>): Completable

    fun postNewAnnouncement(pinned: Boolean,
                            locked: Boolean,
                            asUserUid: String,
                            statusText: String,
                            announcementType: AnnouncementType,
                            audiences: List<Audience>): Completable

    fun postNewPoll(pinned: Boolean,
                    locked: Boolean,
                    asUserUid: String,
                    questionText: String,
                    choices: List<String>,
                    duration: Duration?): Completable
}