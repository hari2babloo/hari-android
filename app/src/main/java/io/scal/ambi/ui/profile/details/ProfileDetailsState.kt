package io.scal.ambi.ui.profile.details

import android.databinding.ObservableList
import io.scal.ambi.ui.home.newsfeed.list.data.UIModelFeed

internal sealed class ProfileDetailsProgressState {

    object TotalProgress : ProfileDetailsProgressState()

    object NoProgress : ProfileDetailsProgressState()

    object EmptyProgress : ProfileDetailsProgressState()

    object RefreshProgress : ProfileDetailsProgressState()

    object PageProgress : ProfileDetailsProgressState()
}

internal sealed class ProfileDetailsErrorState {

    object NoErrorState : ProfileDetailsErrorState()

    class FatalErrorState(val error: String) : ProfileDetailsErrorState()

    class NonFatalErrorState(val error: String) : ProfileDetailsErrorState()
}

sealed class ProfileDetailsDataState(open val profileInfo: UIProfile?) {

    data class DataInfoOnly(override val profileInfo: UIProfile) : ProfileDetailsDataState(profileInfo)

    data class DataNewsFeedEmpty(override val profileInfo: UIProfile?) : ProfileDetailsDataState(profileInfo)

    data class DataNewsFeed(override val profileInfo: UIProfile?, val newsFeed: ObservableList<UIModelFeed>) : ProfileDetailsDataState(profileInfo)

    fun copyProfileInfo(uiProfile: UIProfile): ProfileDetailsDataState =
        when (this) {
            is DataInfoOnly      -> copy(profileInfo = uiProfile)
            is DataNewsFeedEmpty -> copy(profileInfo = uiProfile)
            is DataNewsFeed      -> copy(profileInfo = uiProfile)
        }
}