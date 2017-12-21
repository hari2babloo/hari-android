package io.scal.ambi.model.interactor.home.newsfeed.creation

import io.scal.ambi.entity.user.User
import io.scal.ambi.entity.feed.Audience

class StatusUpdate(val pinned: Boolean,
                   val locked: Boolean,
                   val selectedAsUser: User,
                   val statusText: String,
                   val audiences: Audience)