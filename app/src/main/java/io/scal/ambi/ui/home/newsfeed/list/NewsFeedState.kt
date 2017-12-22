package io.scal.ambi.ui.home.newsfeed.list

import android.databinding.ObservableList
import io.scal.ambi.ui.home.newsfeed.list.data.UIModelFeed

internal sealed class NewsFeedProgressState {

    object NoProgress : NewsFeedProgressState()

    object EmptyProgress : NewsFeedProgressState()

    object RefreshProgress : NewsFeedProgressState()

    object PageProgress : NewsFeedProgressState()
}

internal sealed class NewsFeedErrorState {

    object NoErrorState : NewsFeedErrorState()

    class FatalErrorState(val error: String) : NewsFeedErrorState()

    class NonFatalErrorState(val error: String) : NewsFeedErrorState()
}

internal sealed class NewsFeedDataState {

    object Empty : NewsFeedDataState()

    class Data(val newsFeed: ObservableList<UIModelFeed>) : NewsFeedDataState()
}