package io.scal.ambi.model.interactor.home.newsfeed.creation

import io.scal.ambi.entity.user.User
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.entity.feed.PollChoice
import io.scal.ambi.entity.feed.PollEndsTime

class PollCreation(val pinned: Boolean,
                   val locked: Boolean,
                   val selectedAsUser: User,
                   val questionText: String,
                   val choices: List<PollChoice>,
                   val pollDuration: PollEndsTime,
                   val audiences: Audience)