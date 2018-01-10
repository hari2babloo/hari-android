package io.scal.ambi.ui.home.newsfeed.creation.announcement

import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.binding.observable.ObservableString

sealed class AnnouncementCreationDataState(open val pinned: Boolean = false,
                                           open val locked: Boolean = false,
                                           open val selectedAsUser: User? = null,
                                           open val announcementText: ObservableString? = null) {

    open val asUsers: List<User>? = null

    internal data class Data(override val pinned: Boolean,
                             override val locked: Boolean,
                             override val selectedAsUser: User,
                             override val asUsers: List<User>,
                             override val announcementText: ObservableString) : AnnouncementCreationDataState() {

        init {
            if (!asUsers.contains(selectedAsUser)) {
                throw IllegalStateException("selected user should be in all as users")
            }
        }
    }
}

sealed class AnnouncementCreationProgressState(val progress: Boolean) {

    object Progress : AnnouncementCreationProgressState(true)

    object NoProgress : AnnouncementCreationProgressState(false)
}

sealed class AnnouncementCreationErrorState(open val message: String?) {

    object NoError : AnnouncementCreationErrorState(null)

    class Error(override val message: String) : AnnouncementCreationErrorState(message)

    class ErrorFatal(override val message: String) : AnnouncementCreationErrorState(message)
}