package io.scal.ambi.model.interactor.home.newsfeed.creation

import io.reactivex.Completable
import io.reactivex.Single
import io.scal.ambi.entity.User
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.entity.feed.NewsFeedItemPollCreation

interface IPollsCreationInteractor {

    abstract val availableAudiences: List<Audience>

    fun loadAsUsers(): Single<List<User>>

    fun postPoll(newsFeedItemPollCreation: NewsFeedItemPollCreation): Completable
}