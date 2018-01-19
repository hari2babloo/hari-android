package io.scal.ambi.ui.home.newsfeed.creation.status

import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.binding.observable.ObservableString

sealed class StatusUpdateDataState(open val pinned: Boolean = false,
                                   open val locked: Boolean = false,
                                   open val selectedAsUser: User? = null,
                                   open val statusText: ObservableString? = null) {

    open val asUsers: List<User>? = null

    internal data class Data(override val pinned: Boolean,
                             override val locked: Boolean,
                             override val selectedAsUser: User,
                             override val asUsers: List<User>,
                             override val statusText: ObservableString) : StatusUpdateDataState() {

        init {
            if (!asUsers.contains(selectedAsUser)) {
                throw IllegalStateException("selected user should be in all as users")
            }
        }
    }
}

sealed class StatusUpdateProgressState(val progress: Boolean) {

    object Progress : StatusUpdateProgressState(true)

    object NoProgress : StatusUpdateProgressState(false)
}

sealed class StatusUpdateErrorState(open val message: String?) {

    object NoError : StatusUpdateErrorState(null)

    class Error(override val message: String) : StatusUpdateErrorState(message)

    class ErrorFatal(override val message: String) : StatusUpdateErrorState(message)
}