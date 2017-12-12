package io.scal.ambi.entity.feed

import io.scal.ambi.entity.User
import io.scal.ambi.extensions.binding.observable.ObservableString
import io.scal.ambi.ui.home.newsfeed.creation.polls.PollsCreationChoiceViewModel

data class NewsFeedItemPollCreation(val pinned: Boolean,
                                    val locked: Boolean,
                                    val selectedAsUser: User,
                                    val questionText: ObservableString,
                                    val choices: List<PollsCreationChoiceViewModel>,
                                    val selectedPollDuration: PollsEndsTime,
                                    val selectedAudience: Audience)