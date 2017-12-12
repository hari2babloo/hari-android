package io.scal.ambi.entity.feed

import io.scal.ambi.entity.User

data class NewsFeedItemPollCreation(val pinned: Boolean,
                                    val locked: Boolean,
                                    val selectedAsUser: User,
                                    val questionText: String,
                                    val choices: List<String>,
                                    val selectedPollDuration: PollsEndsTime,
                                    val selectedAudience: Audience)