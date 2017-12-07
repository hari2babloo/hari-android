package io.scal.ambi.model.interactor.home.newsfeed

import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.User
import io.scal.ambi.entity.feed.NewsFeedItem
import io.scal.ambi.model.interactor.auth.profile.IAuthProfileCheckerInteractor
import javax.inject.Inject

class NewsFeedInteractor @Inject constructor(private val profileInteractor: IAuthProfileCheckerInteractor) : INewsFeedInteractor {

    override fun loadNewsFeedPage(page: Int): Single<List<NewsFeedItem>> {
        return Single.error(UnsupportedOperationException("not implemented"))
    }

    override fun loadCurrentUser(): Observable<User> =
        profileInteractor.getUserProfile()
            .onErrorResumeNext(Observable.never<User>())
}