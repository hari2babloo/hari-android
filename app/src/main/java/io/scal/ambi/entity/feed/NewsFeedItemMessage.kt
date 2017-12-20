package io.scal.ambi.entity.feed

import io.scal.ambi.entity.User
import io.scal.ambi.entity.actions.Comment
import org.joda.time.DateTime

data class NewsFeedItemMessage(val uid: String,
                               val pinned: Boolean,
                               val locked: Boolean,
                               val user: User,
                               val messageText: String,
                               var messageCreatedAt: DateTime,
                               val announcement: Announcement?,
                               val comments: List<Comment>,
                               val likes: List<User>) : NewsFeedItem