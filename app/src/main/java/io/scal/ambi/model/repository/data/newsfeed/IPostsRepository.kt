package io.scal.ambi.model.repository.data.newsfeed

import io.reactivex.Completable
import io.reactivex.Single
import io.scal.ambi.entity.actions.Comment
import io.scal.ambi.entity.feed.*
import org.joda.time.DateTime

interface IPostsRepository {

    fun loadPostsGeneral(entityType: String, page: Long, audience: Audience): Single<List<NewsFeedItem>>

    fun loadPostsPersonal(page: Long): Single<List<NewsFeedItem>>

    fun loadPostsForUser(profileUid: String, page: Long): Single<List<NewsFeedItem>>

    fun postNewStatus(pinned: Boolean,
                      locked: Boolean,
                      asUserUid: String,
                      statusText: String,
                      audiences: List<Audience>,
                      fileId: String?): Completable

    fun postNewAnnouncement(pinned: Boolean,
                            locked: Boolean,
                            asUserUid: String,
                            statusText: String,
                            announcementType: AnnouncementType,
                            audiences: List<Audience>,
                            fileId: String?): Completable

    fun postNewPoll(pinned: Boolean,
                    locked: Boolean,
                    asUserUid: String,
                    questionText: String,
                    choices: List<String>,
                    pollEndsTime: DateTime?): Completable

    fun sendUserCommentToPost(newsFeedItem: NewsFeedItem, userCommentText: String): Single<Comment>

    fun changeUserLikeForPost(feedItem: NewsFeedItem, like: Boolean): Completable

    fun answerForPoll(feedItemPoll: NewsFeedItemPoll, pollChoice: PollChoice): Single<NewsFeedItem>
}