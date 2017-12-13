package io.scal.ambi.entity.feed

import io.scal.ambi.entity.User

data class NewsFeedItemPoll(val pinned: Boolean,
                            val locked: Boolean,
                            val selectedAsUser: User,
                            val questionText: String,
                            val choices: List<PollChoice>,
                            val selectedPollDuration: PollEndsTime,
                            val selectedAudience: Audience)