package io.scal.ambi.ui.home.notifications
import android.databinding.ObservableList

internal sealed class NotificationState {

    object TotalProgress : NotificationState()

    object NoProgress : NotificationState()

    object EmptyProgress : NotificationState()

    object RefreshProgress : NotificationState()

    object PageProgress : NotificationState()
}

internal sealed class NotificationErrorState {

    object NoErrorState : NotificationErrorState()

    class FatalErrorState(val error: String) : NotificationErrorState()

    class NonFatalErrorState(val error: String) : NotificationErrorState()
}

sealed class NotificationDataState(open val profileInfo: NotificationData?) {

    data class DataInfoOnly(override val profileInfo: NotificationData) : NotificationDataState(profileInfo)

    data class NotificationEmpty(override val profileInfo: NotificationData?) : NotificationDataState(profileInfo)

    data class NotificationFeed(override val profileInfo: NotificationData?, val newsFeed: ObservableList<NotificationData>) : NotificationDataState(profileInfo)

    fun copyNotificationInfo(uiProfile: NotificationData): NotificationDataState =
        when (this) {
            is DataInfoOnly      -> copy(profileInfo = uiProfile)
            is NotificationEmpty -> copy(profileInfo = uiProfile)
            is NotificationFeed -> copy(profileInfo = uiProfile)
        }
}