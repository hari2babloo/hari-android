package io.scal.ambi.ui.home.newsfeed.list.adapter

import android.databinding.ObservableField
import io.scal.ambi.entity.user.User
import io.scal.ambi.ui.home.newsfeed.list.data.UIModelFeed

interface INewsFeedViewModel {

    val currentUser: ObservableField<User>

    fun openAuthorOf(element: UIModelFeed)

    fun openCommentsOf(element: UIModelFeed)

    fun changeUserLikeOf(element: UIModelFeed)

    fun sendCommentForElement(element: UIModelFeed)

    fun selectPollChoice(element: UIModelFeed.Poll, choice: UIModelFeed.Poll.PollChoiceResult)

}