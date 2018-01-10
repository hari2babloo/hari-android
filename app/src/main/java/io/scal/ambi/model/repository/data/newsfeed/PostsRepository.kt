package io.scal.ambi.model.repository.data.newsfeed

import io.reactivex.Completable
import io.reactivex.Single
import io.scal.ambi.entity.feed.AnnouncementType
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.entity.feed.NewsFeedItem
import io.scal.ambi.model.data.server.*
import org.joda.time.Duration
import javax.inject.Inject

class PostsRepository @Inject constructor(private val postsApi: PostsApi) : IPostsRepository {

    override fun loadPostsGeneral(lastPostTime: Long?): Single<List<NewsFeedItem>> {
        return postsApi.getPostsGeneral(lastPostTime)
            .map { it.parse() }
    }

    override fun postNewStatus(pinned: Boolean,
                               locked: Boolean,
                               asUserUid: String,
                               statusText: String,
                               audiences: List<Audience>): Completable {
        return postsApi
            .postNewStatus(
                StatusCreationRequest(asUserUid,
                                      pinned,
                                      locked,
                                      statusText,
                                      listOf(Host(PostHostKind.USER.toServerName(), asUserUid)),
                                      audiences.map { it.toServerName() }
                )
            )
    }

    override fun postNewAnnouncement(pinned: Boolean,
                                     locked: Boolean,
                                     asUserUid: String,
                                     statusText: String,
                                     announcementType: AnnouncementType,
                                     audiences: List<Audience>): Completable {
        return postsApi
            .postNewAnnouncement(
                AnnouncementCreationRequest(asUserUid,
                                            pinned,
                                            locked,
                                            statusText,
                                            listOf(Host(PostHostKind.USER.toServerName(), asUserUid)),
                                            announcementType.toServerName(),
                                            audiences.map { it.toServerName() }
                )
            )
    }

    override fun postNewPoll(pinned: Boolean,
                             locked: Boolean,
                             asUserUid: String,
                             questionText: String,
                             choices: List<String>,
                             duration: Duration?): Completable {
        return postsApi
            .postNewPoll(
                PollCreationRequest(asUserUid,
                                    pinned,
                                    locked,
                                    questionText,
                                    choices.map { ChoiceRequest(it) },
                                    duration?.millis,
                                    listOf(Host(PostHostKind.USER.toServerName(), asUserUid))
                )
            )
    }
}

private fun Audience.toServerName(): String =
    when (this) {
        Audience.STUDENTS -> "Student"
        Audience.FACULTY  -> "Faculty"
        Audience.STAFF    -> "Staff"
        else              -> throw IllegalArgumentException("$this is not listed in server requests names")
    }

private fun PostHostKind.toServerName(): String =
    when (this) {
        PostHostKind.USER -> "User"
        else              -> throw IllegalArgumentException("$this is not supported yet")
    }

@Suppress("REDUNDANT_ELSE_IN_WHEN")
private fun AnnouncementType.toServerName(): String =
    when (this) {
        AnnouncementType.SAFETY    -> "safety"
        AnnouncementType.GOOD_NEWS -> "good news"
        AnnouncementType.EVENT     -> "event"
        AnnouncementType.TRAGEDY   -> "tragedy"
        AnnouncementType.GENERAL   -> "general"
        else                       -> throw IllegalArgumentException("unknown announcementType type: $this")
    }