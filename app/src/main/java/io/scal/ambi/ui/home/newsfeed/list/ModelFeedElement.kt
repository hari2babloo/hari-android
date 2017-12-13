package io.scal.ambi.ui.home.newsfeed.list

import io.scal.ambi.entity.User
import io.scal.ambi.entity.actions.ElementComments
import io.scal.ambi.entity.actions.ElementLikes
import io.scal.ambi.entity.feed.Announcement
import io.scal.ambi.extensions.view.IconImage
import org.joda.time.LocalDateTime

sealed class ModelFeedElement(val uid: String,
                              val actor: String,
                              val icon: IconImage,
                              val dateTime: LocalDateTime,
                              val locked: Boolean,
                              val pinned: Boolean,
                              var announcement: Announcement?) {

    class Message(uid: String,
                  val author: User,
                  icon: IconImage,
                  dateTime: LocalDateTime,
                  locked: Boolean,
                  pinned: Boolean,
                  announcement: Announcement?,
                  val message: String,
                  val likes: ElementLikes,
                  val comments: ElementComments) : ModelFeedElement(uid, author.name, icon, dateTime, locked, pinned, announcement)

    class Link(uid: String,
               actor: String,
               icon: IconImage,
               dateTime: LocalDateTime,
               locked: Boolean,
               pinned: Boolean,
               announcement: Announcement?,
               val message: String,
               val linkUri: String,
               val linkPreviewImage: IconImage?,
               val linkTitle: String,
               val likes: ElementLikes,
               val comments: ElementComments) : ModelFeedElement(uid, actor, icon, dateTime, locked, pinned, announcement)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ModelFeedElement) return false

        return uid == other.uid
    }

    override fun hashCode(): Int =
        uid.hashCode()
}