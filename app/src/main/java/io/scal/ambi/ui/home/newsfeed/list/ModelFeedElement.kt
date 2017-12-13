package io.scal.ambi.ui.home.newsfeed.list

import io.scal.ambi.entity.User
import io.scal.ambi.entity.actions.ElementComments
import io.scal.ambi.entity.actions.ElementLikes
import io.scal.ambi.entity.feed.Announcement
import io.scal.ambi.entity.feed.PollChoice
import io.scal.ambi.extensions.view.IconImage
import org.joda.time.LocalDateTime

sealed class ModelFeedElement(open val uid: String,
                              open val actor: String,
                              open val icon: IconImage,
                              open val dateTime: LocalDateTime,
                              open val locked: Boolean,
                              open val pinned: Boolean,
                              open val announcement: Announcement?) {

    data class Message(override val uid: String,
                       val author: User,
                       override val dateTime: LocalDateTime,
                       override val locked: Boolean,
                       override val pinned: Boolean,
                       override val announcement: Announcement?,
                       val message: String,
                       val likes: ElementLikes,
                       val comments: ElementComments) : ModelFeedElement(uid, author.name, author.avatar, dateTime, locked, pinned, announcement)

    data class Poll(override val uid: String,
                    val author: User,
                    override val dateTime: LocalDateTime,
                    override val locked: Boolean,
                    override val pinned: Boolean,
                    override val announcement: Announcement?,
                    val question: String,
                    val choices: List<PollChoiceResult>,
                    val userChoice: PollChoice?,
                    val pollEndsDateTime: LocalDateTime?,
                    val likes: ElementLikes,
                    val comments: ElementComments) : ModelFeedElement(uid, author.name, author.avatar, dateTime, locked, pinned, announcement) {

        init {
            if (choices.isEmpty()) {
                throw IllegalArgumentException("choices can not be empty")
            }
        }

        data class PollChoiceResult(val pollChoice: PollChoice, val totalVotes: Int, val mostVoted: Boolean)
    }

    data class Link(override val uid: String,
                    override val actor: String,
                    override val icon: IconImage,
                    override val dateTime: LocalDateTime,
                    override val locked: Boolean,
                    override val pinned: Boolean,
                    override val announcement: Announcement?,
                    val message: String,
                    val linkUri: String,
                    val linkPreviewImage: IconImage?,
                    val linkTitle: String,
                    val likes: ElementLikes,
                    val comments: ElementComments) : ModelFeedElement(uid, actor, icon, dateTime, locked, pinned, announcement)
}