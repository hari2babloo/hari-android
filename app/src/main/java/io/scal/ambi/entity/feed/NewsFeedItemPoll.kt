package io.scal.ambi.entity.feed

import io.scal.ambi.entity.actions.Comment
import io.scal.ambi.entity.user.User
import org.joda.time.DateTime

data class NewsFeedItemPoll(val uid: String,
                            val pinned: Boolean,
                            val locked: Boolean,
                            val user: User,
                            val questionText: String,
                            val choices: List<PollChoice>,
                            var pollCreatedAt: DateTime,
                            val pollEndsTime: DateTime?,
                            val audiences: List<Audience>,
                            val comments: List<Comment>,
                            val likes: List<User>) : NewsFeedItem {
    init {
        if (audiences.isEmpty()) {
            throw IllegalArgumentException("audience can not be null")
        }
    }
}