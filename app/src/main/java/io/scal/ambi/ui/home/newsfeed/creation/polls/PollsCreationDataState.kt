package io.scal.ambi.ui.home.newsfeed.creation.polls

import io.scal.ambi.entity.user.User
import io.scal.ambi.entity.feed.PollEndsTime
import io.scal.ambi.extensions.binding.observable.ObservableString

sealed class PollsCreationDataState(open val pinned: Boolean = false,
                                    open val locked: Boolean = false,
                                    open val selectedAsUser: User? = null,
                                    open val questionText: ObservableString? = null,
                                    open val choices: List<PollsCreationChoiceViewModel>? = null,
                                    open val selectedPollDuration: PollEndsTime? = null) {

    open val asUsers: List<User>? = null
    open val pollDurations: List<PollEndsTime>? = null

    internal data class Data(override val pinned: Boolean,
                             override val locked: Boolean,
                             override val selectedAsUser: User,
                             override val asUsers: List<User>,
                             override val questionText: ObservableString,
                             override val choices: List<PollsCreationChoiceViewModel>,
                             override val selectedPollDuration: PollEndsTime,
                             override val pollDurations: List<PollEndsTime>) : PollsCreationDataState() {

        init {
            if (!asUsers.contains(selectedAsUser)) {
                throw IllegalStateException("selected user should be in all as users")
            }
            if (!pollDurations.contains(selectedPollDuration)) {
                throw IllegalStateException("selected poll duration should be in all as poll durations")
            }
        }
    }
}

sealed class PollsCreationProgressState(val progress: Boolean) {

    object Progress : PollsCreationProgressState(true)

    object NoProgress : PollsCreationProgressState(false)
}

sealed class PollsCreationErrorState(open val message: String?) {

    object NoError : PollsCreationErrorState(null)

    class Error(override val message: String) : PollsCreationErrorState(message)

    class ErrorFatal(override val message: String) : PollsCreationErrorState(message)
}