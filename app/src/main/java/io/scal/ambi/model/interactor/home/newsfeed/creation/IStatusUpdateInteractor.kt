package io.scal.ambi.model.interactor.home.newsfeed.creation

import io.reactivex.Completable
import io.reactivex.Single
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.entity.user.User

interface IStatusUpdateInteractor {

    val availableAudiences: List<Audience>

    fun loadAsUsers(): Single<List<User>>

    fun updateStatus(statusUpdate: StatusUpdate): Completable
}