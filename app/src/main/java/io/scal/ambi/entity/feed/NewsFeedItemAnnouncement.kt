package io.scal.ambi.entity.feed

import io.scal.ambi.entity.actions.Comment
import io.scal.ambi.entity.chat.ChatAttachment
import io.scal.ambi.entity.user.User
import org.joda.time.DateTime

class NewsFeedItemAnnouncement(val uid: String,
                               val pinned: Boolean,
                               val locked: Boolean,
                               val user: User,
                               val messageText: String,
                               var messageCreatedAt: DateTime,
                               val audiences: List<Audience>,
                               val announcementType: AnnouncementType,
                               val comments: List<Comment>,
                               val likes: List<User>,
                               val attachments: List<ChatAttachment>) : NewsFeedItem {

    init {
        if (audiences.isEmpty()) {
            throw IllegalArgumentException("audience can not be null")
        }
    }
}