package io.scal.ambi.ui.home.newsfeed.list

import android.databinding.ObservableList

internal sealed class NewsFeedProgressState {

    object NoProgress : NewsFeedProgressState()

    object EmptyProgress : NewsFeedProgressState()

    object RefreshProgress : NewsFeedProgressState()

    object PageProgress : NewsFeedProgressState()
}

internal sealed class NewsFeedErrorState {

    open val fatal: Boolean? = null

    object NoErrorState : NewsFeedErrorState()

    class FatalErrorState(val error: Throwable) : NewsFeedErrorState() {

        override val fatal: Boolean = true
    }

    class NonFatalErrorState(val error: Throwable) : NewsFeedErrorState() {

        override val fatal: Boolean = false
    }
}

internal sealed class NewsFeedDataState {

    object Empty : NewsFeedDataState()

    class Data(val newsFeed: ObservableList<ModelFeedElement>) : NewsFeedDataState()
}