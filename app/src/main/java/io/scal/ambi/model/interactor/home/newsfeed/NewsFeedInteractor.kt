package io.scal.ambi.model.interactor.home.newsfeed

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.User
import io.scal.ambi.entity.feed.NewsFeedItem
import io.scal.ambi.entity.feed.PollChoice
import io.scal.ambi.model.repository.data.newsfeed.IPostsRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import org.joda.time.DateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NewsFeedInteractor @Inject constructor(private val postsRepository: IPostsRepository,
                                             private val localUserDataRepository: ILocalUserDataRepository) : INewsFeedInteractor {

    override fun loadCurrentUser(): Observable<User> =
        localUserDataRepository.observeCurrentUser()
            .onErrorResumeNext(Observable.never<User>())

    override fun loadNewsFeedPage(page: Int, dateTime: DateTime?): Single<List<NewsFeedItem>> {
        return postsRepository.loadPostsGeneral(dateTime?.millis)
    }

    override fun answerForPoll(pollChoice: PollChoice, pollUid: String): Single<NewsFeedItem> =
        Completable.complete()
            .delay(3, TimeUnit.SECONDS)
            .andThen(Single.error(IllegalArgumentException("sdfsdfsdf")))
}