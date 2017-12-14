package io.scal.ambi.model.interactor.home.newsfeed.creation

import io.reactivex.Single
import io.scal.ambi.entity.User
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.entity.feed.NewsFeedItem

interface IPollsCreationInteractor {

    val availableAudiences: List<Audience>

    fun loadAsUsers(): Single<List<User>>

    fun postPoll(pollCreation: PollCreation): Single<NewsFeedItem>
}