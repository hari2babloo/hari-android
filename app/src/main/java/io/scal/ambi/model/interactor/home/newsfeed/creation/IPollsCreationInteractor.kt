package io.scal.ambi.model.interactor.home.newsfeed.creation

import io.reactivex.Single
import io.scal.ambi.entity.feed.NewsFeedItem
import io.scal.ambi.entity.user.User

interface IPollsCreationInteractor {

    fun loadAsUsers(): Single<List<User>>

    fun postPoll(pollCreation: PollCreation): Single<NewsFeedItem>
}