package io.scal.ambi.ui.home.newsfeed.list.data

import io.scal.ambi.entity.feed.AnnouncementType
import io.scal.ambi.entity.feed.NewsFeedItem
import io.scal.ambi.entity.feed.NewsFeedItemPoll
import io.scal.ambi.entity.feed.PollChoice
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.binding.observable.ObservableString
import io.scal.ambi.extensions.view.IconImage
import org.joda.time.DateTime

sealed class UIModelFeed(open val uid: String,
                         open val feedItem: NewsFeedItem,
                         open val actor: String,
                         open val icon: IconImage,
                         open val createdAtDateTime: DateTime,
                         open val locked: Boolean,
                         open val pinned: Boolean,
                         open val announcementType: AnnouncementType?,
                         open val likes: UILikes,
                         open val comments: UIComments,
                         open val userCommentText: ObservableString) {

    data class Message(override val uid: String,
                       override val feedItem: NewsFeedItem,
                       val author: User,
                       override val createdAtDateTime: DateTime,
                       override val locked: Boolean,
                       override val pinned: Boolean,
                       override val announcementType: AnnouncementType?,
                       val message: String,
                       override val likes: UILikes,
                       override val comments: UIComments,
                       override val userCommentText: ObservableString) :
        UIModelFeed(uid,
                    feedItem,
                    author.name,
                    author.avatar,
                    createdAtDateTime,
                    locked,
                    pinned,
                    announcementType,
                    likes,
                    comments,
                    userCommentText) {

        override fun updateComments(uiComments: UIComments): UIModelFeed = copy(comments = uiComments)
    }

    data class Poll(override val uid: String,
                    override val feedItem: NewsFeedItemPoll,
                    val author: User,
                    override val createdAtDateTime: DateTime,
                    override val locked: Boolean,
                    override val pinned: Boolean,
                    override val announcementType: AnnouncementType?,
                    val question: String,
                    val choices: List<PollChoiceResult>,
                    val userChoice: PollChoice?,
                    val pollEndsDateTime: DateTime?,
                    override val likes: UILikes,
                    override val comments: UIComments,
                    override val userCommentText: ObservableString) :
        UIModelFeed(uid,
                    feedItem,
                    author.name,
                    author.avatar,
                    createdAtDateTime,
                    locked,
                    pinned,
                    announcementType,
                    likes,
                    comments,
                    userCommentText) {

        init {
            if (choices.isEmpty()) {
                throw IllegalArgumentException("choices can not be empty")
            }
        }

        override fun updateComments(uiComments: UIComments): UIModelFeed = copy(comments = uiComments)

        data class PollChoiceResult(val pollChoice: PollChoice, val totalVotes: Int, val mostVoted: Boolean)
    }

    data class Link(override val uid: String,
                    override val feedItem: NewsFeedItem,
                    override val actor: String,
                    override val icon: IconImage,
                    override val createdAtDateTime: DateTime,
                    override val locked: Boolean,
                    override val pinned: Boolean,
                    override val announcementType: AnnouncementType?,
                    val message: String,
                    val linkUri: String,
                    val linkPreviewImage: IconImage?,
                    val linkTitle: String,
                    override val likes: UILikes,
                    override val comments: UIComments,
                    override val userCommentText: ObservableString) :
        UIModelFeed(uid, feedItem, actor, icon, createdAtDateTime, locked, pinned, announcementType, likes, comments, userCommentText) {

        override fun updateComments(uiComments: UIComments): UIModelFeed = copy(comments = uiComments)
    }

    abstract fun updateComments(uiComments: UIComments): UIModelFeed
}