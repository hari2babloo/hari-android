package io.scal.ambi.model.interactor.home.newsfeed

import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.User
import io.scal.ambi.entity.feed.NewsFeedItem
import io.scal.ambi.model.repository.data.newsfeed.IPostsRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import org.joda.time.LocalDateTime
import javax.inject.Inject

class NewsFeedInteractor @Inject constructor(private val postsRepository: IPostsRepository,
                                             private val localUserDataRepository: ILocalUserDataRepository) : INewsFeedInteractor {

    override fun loadNewsFeedPage(page: Int, dateTime: LocalDateTime?): Single<List<NewsFeedItem>> {
        return postsRepository.loadPostsGeneral(null)
    }

    override fun loadCurrentUser(): Observable<User> =
        localUserDataRepository.observeCurrentUser()
            .onErrorResumeNext(Observable.never<User>())
}