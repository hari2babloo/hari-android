package io.scal.ambi.model.repository.data.newsfeed

import com.google.gson.*
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

    override fun loadPostsGeneral(page: Long, audience: Audience): Single<List<NewsFeedItem>> {
        return postsApi.getPostsGeneral(page, audience.toServerName())
            .map { it.parse() }
    }

    override fun loadPostsPersonal(page: Long): Single<List<NewsFeedItem>> {
        return postsApi.getPostsPersonal(page)
            .map { it.parse() }
    }

    override fun loadPostsForUser(profileUid: String, page: Long): Single<List<NewsFeedItem>> {
        return postsApi.getPostsForUser(profileUid, page)
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
        return getPostWithCurrentUserInfo(newsFeedItem, "comments")
            .map { pair ->
                val comments = pair.second as JsonArray

                val newComment = JsonObject()
                newComment.add("commenter", JsonPrimitive(pair.first.uid))
                newComment.add("text", JsonPrimitive(userCommentText))

                val newComments = JsonArray()
                newComments.add(newComment)
                comments.iterator().forEach { newComments.add(it) }

                val postJsonItem = JsonObject()
                postJsonItem.add("comments", newComments)
                Pair(pair.first, postJsonItem.toString())
            }
            .flatMap { pair -> sendPostWithUpdatedInfo(newsFeedItem, pair) }
            .map { user -> Comment(UUID.randomUUID().toString(), user, userCommentText, DateTime.now()) }
    }

    override fun changeUserLikeForPost(feedItem: NewsFeedItem, like: Boolean): Completable {
        return getPostWithCurrentUserInfo(feedItem, "likes")
            .map { pair ->
                val likes = pair.second as JsonArray

                val newLikes = JsonArray()
                likes
                    .map {
                        if (it.isJsonObject)
                            it.asJsonObject.getAsJsonPrimitive("id").asString
                        else
                            it.asString
                    }
                    .filter { it != pair.first.uid }
                    .forEach { newLikes.add(it) }
                if (like) {
                    newLikes.add(JsonPrimitive(pair.first.uid))
                }

                val postJsonItem = JsonObject()
                postJsonItem.add("likes", newLikes)
                Pair(pair.first, postJsonItem.toString())
            }
            .flatMap { pair -> sendPostWithUpdatedInfo(feedItem, pair) }
            .toCompletable()
    }

    override fun answerForPoll(feedItemPoll: NewsFeedItemPoll, pollChoice: PollChoice): Single<NewsFeedItem> {
        return getPostWithCurrentUserInfo(feedItemPoll, "answerChoices")
            .map { pair ->
                val answerChoices = pair.second as JsonArray

                val selectedAnswer = answerChoices
                    .map { it.asJsonObject }
                    .first { it.getAsJsonPrimitive("text").asString == pollChoice.text }

                val selectedAnswerVoters = selectedAnswer.get("voters").asJsonArray
                if (null == selectedAnswerVoters
                    .map { it as JsonPrimitive }
                    .firstOrNull { it.asString == pair.first.uid }) {

                    selectedAnswerVoters.add(pair.first.uid)
                }

                val postJsonItem = JsonObject()
                postJsonItem.add("answerChoices", answerChoices)
                Pair(pair.first, postJsonItem.toString())
            }
            .flatMap { pair -> sendPostWithUpdatedInfo(feedItemPoll, pair) }
            .map { user ->
                val choices = feedItemPoll.choices.map { if (it.uid == pollChoice.uid) it.copy(voters = it.voters.plus(user.uid)) else it }
                feedItemPoll.copy(choices = choices)
            }
    }

    private fun getPostWithCurrentUserInfo(newsFeedItem: NewsFeedItem, childToReturn: String?): Single<Pair<User, JsonElement>> {
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
                Pair(pair.first, childToReturn?.let { postJsonItem.get(it) } ?: postJsonItem)
            }
    }

    private fun sendPostWithUpdatedInfo(newsFeedItem: NewsFeedItem, pair: Pair<User, String>): Single<User> {
        val contentType = "application/json"
        return when (newsFeedItem) {
            is NewsFeedItemUpdate       -> postsApi.updateStatusPost(newsFeedItem.uid, contentType, pair.second)
            is NewsFeedItemAnnouncement -> postsApi.updateAnnouncementPost(newsFeedItem.uid, contentType, pair.second)
            is NewsFeedItemPoll         -> postsApi.updatePollPost(newsFeedItem.uid, contentType, pair.second)
            else                        -> throw IllegalArgumentException("unknown news feed type")
        }
            .andThen(Single.just(pair.first))
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