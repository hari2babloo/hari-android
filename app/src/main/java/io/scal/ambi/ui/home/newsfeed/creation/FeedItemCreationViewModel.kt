package io.scal.ambi.ui.home.newsfeed.creation

import android.databinding.ObservableField
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import javax.inject.Inject
import javax.inject.Named

class FeedItemCreationViewModel @Inject constructor(router: BetterRouter,
                                                    @Named("selectedFeedItemCreation") feedItemCreation: FeedItemCreation) : BaseViewModel(router) {

    val selectedFeedItem = ObservableField<FeedItemCreation>(feedItemCreation)

    fun createStatus() {
        selectedFeedItem.set(FeedItemCreation.STATUS)
    }

    fun createAnnouncement() {
        selectedFeedItem.set(FeedItemCreation.ANNOUNCEMENT)
    }

    fun createPoll() {
        selectedFeedItem.set(FeedItemCreation.POLL)
    }
}