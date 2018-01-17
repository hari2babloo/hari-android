package io.scal.ambi.model.data.server.responses.newsfeed

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.entity.feed.*
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.notNullOrThrow
import io.scal.ambi.model.data.server.responses.user.ItemUser
import io.scal.ambi.model.data.server.responses.Parceble
import org.joda.time.DateTime
import timber.log.Timber

internal class ItemPost : Parceble<NewsFeedItem?> {

    @SerializedName("_id")
    @Expose
    internal var id: String? = null

    @SerializedName("kind")
    @Expose
    internal var kind: String? = null


    @SerializedName("isLocked")
    @Expose
    internal var locked: Boolean? = false

    @SerializedName("isPinned")
    @Expose
    internal var pinned: Boolean? = false

    @SerializedName("poster")
    @Expose
    internal var poster: ItemUser? = null

    @SerializedName("textContent")
    @Expose
    internal var textContent: String? = null

    @SerializedName("createdAt")
    @Expose
    internal var createdAt: String? = null


    @SerializedName("audience")
    @Expose
    internal var audiences: List<String>? = null


    @SerializedName("comments")
    @Expose
    internal var comments: List<ItemComment>? = null

    @SerializedName("likes")
    @Expose
    internal var likes: List<ItemUser>? = null


    //    poll item
    @SerializedName("answerChoices")
    @Expose
    internal var answerChoices: List<ItemAnswerChoice>? = null

    @SerializedName("pollEndsTime")
    @Expose
    internal var pollEndsTime: String? = null

    @SerializedName("announcementType")
    @Expose
    internal var announcementType: String? = null

    override fun parse(): NewsFeedItem? {
        if (null == id || null == kind) {
            throw IllegalArgumentException("id and kind can not be null")
        }
        return when (kind) {
            "PollPost"         -> parseAsPoll()
            "UpdatePost"       -> parseAsUpdate()
            "AnnouncementPost" -> parseAsAnnouncement()
            else               -> {
                Timber.d(IllegalArgumentException("unknown news feed item kind: $kind"), "we can not parse unknown type: $kind")
                null
            }
        }
    }

    private fun parseAsPoll(): NewsFeedItem =
        NewsFeedItemPoll(id.notNullOrThrow("id"),
                         pinned ?: false,
                         locked ?: false,
                         parsePosterUser(),
                         textContent.orEmpty(),
                         answerChoices.notNullOrThrow("answerChoices").map { it.parse() },
                         createdAt.toDateTime("createdAt"),
                         pollEndsTime.toDateTimePollEnds("pollEndsTime"),
                         audiences.notNullOrThrow("audiences").mapNotNull { it.toAudience() },
                         comments?.map { it.parse() } ?: emptyList(),
                         likes?.map { it.parse() } ?: emptyList()
        )

    private fun parseAsUpdate(): NewsFeedItem =
        NewsFeedItemUpdate(id.notNullOrThrow("id"),
                           pinned ?: false,
                           locked ?: false,
                           parsePosterUser(),
                           textContent.orEmpty(),
                           createdAt.toDateTime("createdAt"),
                           audiences.notNullOrThrow("audiences").mapNotNull { it.toAudience() },
                           comments?.map { it.parse() } ?: emptyList(),
                           likes?.map { it.parse() } ?: emptyList()
        )

    private fun parseAsAnnouncement(): NewsFeedItem {
        return NewsFeedItemAnnouncement(id.notNullOrThrow("id"),
                                        pinned ?: false,
                                        locked ?: false,
                                        parsePosterUser(),
                                        textContent.orEmpty(),
                                        createdAt.toDateTime("createdAt"),
                                        audiences.notNullOrThrow("audiences").mapNotNull { it.toAudience() },
                                        announcementType.toAnnouncement(),
                                        comments?.map { it.parse() } ?: emptyList(),
                                        likes?.map { it.parse() } ?: emptyList()
        )
    }

    private fun parsePosterUser(): User {
        return poster.notNullOrThrow("poster").parse()
    }
}

private fun String?.toAnnouncement(): AnnouncementType =
    when (this?.toLowerCase()) {
        "safety"       -> AnnouncementType.SAFETY
        "good news"    -> AnnouncementType.GOOD_NEWS
        "event update" -> AnnouncementType.EVENT
        "tragedy"      -> AnnouncementType.TRAGEDY
        "general"      -> AnnouncementType.GENERAL
        else           -> {
            Timber.w(IllegalArgumentException("unknown announcementType type: $this. switching to general"))
            AnnouncementType.GENERAL
        }
    }

private fun String.toAudience(): Audience? =
    when (this) {
        "Student" -> Audience.STUDENTS
        "Faculty" -> Audience.FACULTY
        "Staff"   -> Audience.STAFF
        else      -> {
            Timber.d(IllegalArgumentException("unknown audience from server: $this"))
            null
        }
    }

private fun String?.toDateTime(fieldName: String): DateTime {
    val notNullString = notNullOrThrow(fieldName)
    return DateTime.parse(notNullString)
}

private fun String?.toDateTimePollEnds(fieldName: String): DateTime? =
    this?.toDateTime(fieldName)