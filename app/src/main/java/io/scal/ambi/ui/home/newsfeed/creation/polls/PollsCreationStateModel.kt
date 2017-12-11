package io.scal.ambi.ui.home.newsfeed.creation.polls

import io.scal.ambi.entity.User
import io.scal.ambi.extensions.binding.observable.ObservableString
import org.joda.time.Duration

sealed class PollsCreationStateModel(val pinned: Boolean = false,
                                     val locked: Boolean = false,
                                     open val selectedAsUser: User? = null,
                                     open val questionText: ObservableString? = null,
                                     open val choices: List<PollsCreationChoiceViewModel>? = null,
                                     open val pollDuration: Duration? = null) {

    open val progress = false
    open val errorMessage: String? = null

    internal class Data(pinned: Boolean,
                        locked: Boolean,
                        override val selectedAsUser: User,
                        override val questionText: ObservableString,
                        override val choices: List<PollsCreationChoiceViewModel>,
                        override val pollDuration: Duration) : PollsCreationStateModel(pinned, locked)

    internal class EmptyProgress : PollsCreationStateModel() {

        override val progress: Boolean = true
    }

    internal class Progress(pinned: Boolean,
                            locked: Boolean,
                            override val selectedAsUser: User,
                            override val questionText: ObservableString,
                            override val choices: List<PollsCreationChoiceViewModel>,
                            override val pollDuration: Duration) : PollsCreationStateModel(pinned, locked) {

        override val progress: Boolean = true
    }

    internal class ErrorFatal(override val errorMessage: String) : PollsCreationStateModel()

    internal class ErrorNotFatal(pinned: Boolean,
                                 locked: Boolean,
                                 override val selectedAsUser: User,
                                 override val questionText: ObservableString,
                                 override val choices: List<PollsCreationChoiceViewModel>,
                                 override val pollDuration: Duration,
                                 override val errorMessage: String) : PollsCreationStateModel(pinned, locked)
}