package io.scal.ambi.model.interactor.home.newsfeed

import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.User
import io.scal.ambi.entity.feed.NewsFeedItem

interface INewsFeedInteractor {

    fun loadNewsFeedPage(page: Int): Single<List<NewsFeedItem>>

    fun loadCurrentUser(): Observable<User>
}