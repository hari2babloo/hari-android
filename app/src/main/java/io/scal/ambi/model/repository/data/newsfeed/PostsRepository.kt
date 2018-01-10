package io.scal.ambi.model.repository.data.newsfeed

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.scal.ambi.entity.actions.Comment
import io.scal.ambi.entity.feed.*
import io.scal.ambi.entity.user.User
import io.scal.ambi.model.data.server.*
import io.scal.ambi.model.repository.local.LocalUserDataRepository
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject

class PostsRepository @Inject constructor(private val postsApi: PostsApi,
                                          private val localUserDataRepository: LocalUserDataRepository) : IPostsRepository {

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
                             pollEndsTime: DateTime?): Completable {
        return postsApi
            .postNewPoll(
                PollCreationRequest(asUserUid,
                                    pinned,
                                    locked,
                                    questionText,
                                    choices.map { ChoiceRequest(it) },
                                    pollEndsTime?.millis,
                                    listOf(Host(PostHostKind.USER.toServerName(), asUserUid))
                )
            )
    }

    override fun sendUserCommentToPost(newsFeedItem: NewsFeedItem, userCommentText: String): Single<Comment> {
        val currentUserObservable: Single<User> = localUserDataRepository.observeCurrentUser().firstOrError()
        val postAsStringObservable: Single<String> =
            when (newsFeedItem) {
                is NewsFeedItemUpdate       -> postsApi.getStatusPostAsString(newsFeedItem.uid)
                is NewsFeedItemAnnouncement -> postsApi.getAnnouncementPostAsString(newsFeedItem.uid)
                is NewsFeedItemPoll         -> postsApi.getPollPostAsString(newsFeedItem.uid)
                else                        -> throw IllegalArgumentException("unknown news feed type")
            }

        val singlePair: Single<Pair<User, String>> =
            Observable.combineLatest(currentUserObservable.toObservable(),
                                     postAsStringObservable.toObservable(),
                                     BiFunction<User, String, Pair<User, String>> { t1, t2 -> Pair(t1, t2) })
                .firstOrError()

        return singlePair
            .map { Pair(it.first, jsonParser.parse(it.second)) }
            .map { Pair(it.first, it.second as JsonObject) }
            .map { pair ->
                val postJson = pair.second
                val postJsonItem =
                    when (newsFeedItem) {
                        is NewsFeedItemUpdate       -> postJson.getAsJsonObject("updatePost")
                        is NewsFeedItemAnnouncement -> postJson.getAsJsonObject("announcementPost")
                        is NewsFeedItemPoll         -> postJson.getAsJsonObject("pollPost")
                        else                        -> throw IllegalArgumentException("unknown news feed type")
                    }
                val comments = postJsonItem.getAsJsonArray("comments")
                postJsonItem.keySet().toMutableSet().forEach { postJsonItem.remove(it) }
                val newComment = JsonObject()
                newComment.add("user", JsonPrimitive(pair.first.uid))
                newComment.add("text", JsonPrimitive(userCommentText))
                newComment.add("dateCreated", JsonPrimitive(DateTime.now().millis))
                val newComments = JsonArray()
                newComments.add(newComment)
                comments.iterator().forEach { newComments.add(it) }
                postJsonItem.add("comments", newComments)
                Pair(pair.first, postJsonItem.toString())
            }
            .flatMap { pair ->
                val contentType = "application/json"
                when (newsFeedItem) {
                    is NewsFeedItemUpdate       -> postsApi.updateStatusPost(newsFeedItem.uid, contentType, pair.second)
                    is NewsFeedItemAnnouncement -> postsApi.updateAnnouncementPost(newsFeedItem.uid, contentType, pair.second)
                    is NewsFeedItemPoll         -> postsApi.updatePollPost(newsFeedItem.uid, contentType, pair.second)
                    else                        -> throw IllegalArgumentException("unknown news feed type")
                }
                    .andThen(Single.just(pair.first))
            }
            .map { user -> Comment(UUID.randomUUID().toString(), user, userCommentText, DateTime.now()) }
    }

    companion object {
        private val jsonParser = JsonParser()
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