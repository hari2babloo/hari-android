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

sealed class ProfileDetailsDataState(val profileInfo: UIProfile?) {

    class DataInfoOnly(profileInfo: UIProfile) : ProfileDetailsDataState(profileInfo)

    class DataNewsFeedEmpty(profileInfo: UIProfile?) : ProfileDetailsDataState(profileInfo)

    class DataNewsFeed(profileInfo: UIProfile?, val newsFeed: ObservableList<UIModelFeed>) : ProfileDetailsDataState(profileInfo)
}