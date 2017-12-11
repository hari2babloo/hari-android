package io.scal.ambi.ui.home.newsfeed.list

import android.databinding.ObservableList

internal sealed class NewsFeedProgressState {

    object NoProgress : NewsFeedProgressState()

    object EmptyProgress : NewsFeedProgressState()

    object RefreshProgress : NewsFeedProgressState()

    object PageProgress : NewsFeedProgressState()
}

internal sealed class NewsFeedErrorState {

    object NoErrorState : NewsFeedErrorState()

    class FatalErrorState(val error: Throwable) : NewsFeedErrorState()

    class NonFatalErrorState(val error: Throwable) : NewsFeedErrorState()
}

internal sealed class NewsFeedDataState {

    object Empty : NewsFeedDataState()

    class Data(val newsFeed: ObservableList<ModelFeedElement>) : NewsFeedDataState()
}