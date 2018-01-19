package io.scal.ambi.model.interactor.home.newsfeed.creation

import io.scal.ambi.entity.chat.ChatAttachment
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.entity.user.User

class StatusUpdate(val pinned: Boolean,
                   val locked: Boolean,
                   val selectedAsUser: User,
                   val statusText: String,
                   val audiences: Audience,
                   val attachments: List<ChatAttachment>)