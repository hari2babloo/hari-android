package io.scal.ambi.model.interactor.home.newsfeed.creation

import io.scal.ambi.entity.chat.ChatAttachment
import io.scal.ambi.entity.feed.AnnouncementType
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.entity.user.User

class AnnouncementCreation(val pinned: Boolean,
                           val locked: Boolean,
                           val selectedAsUser: User,
                           val announcementText: String,
                           val announcementType: AnnouncementType,
                           val audiences: Audience,
                           val attachments: List<ChatAttachment>)