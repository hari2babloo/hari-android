package io.scal.ambi.ui.home.classes.about

import android.databinding.ObservableList

internal sealed class AboutState {

    object TotalProgress : AboutState()

    object NoProgress : AboutState()

    object EmptyProgress : AboutState()

    object RefreshProgress : AboutState()

    object PageProgress : AboutState()
}

internal sealed class AboutErrorState {

    object NoErrorState : AboutErrorState()

    class FatalErrorState(val error: String) : AboutErrorState()

    class NonFatalErrorState(val error: String) : AboutErrorState()
}

sealed class AboutDataState(open val profileInfo: MembersData?) {

    data class DataInfoOnly(override val profileInfo: MembersData) : AboutDataState(profileInfo)

    data class AboutEmpty(override val profileInfo: MembersData?) : AboutDataState(profileInfo)

    data class AboutFeed(override val profileInfo: MembersData?, val newsFeed: List<Any>) : AboutDataState(profileInfo)

    fun copyAboutInfo(uiProfile: MembersData): AboutDataState =
            when (this) {
                is DataInfoOnly      -> copy(profileInfo = uiProfile)
                is AboutEmpty -> copy(profileInfo = uiProfile)
                is AboutFeed -> copy(profileInfo = uiProfile)
            }
}