package io.scal.ambi.model.interactor.home.newsfeed

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.User
import io.scal.ambi.entity.feed.NewsFeedItem
import io.scal.ambi.entity.feed.PollChoice
import org.joda.time.DateTime

interface INewsFeedInteractor {

    fun loadCurrentUser(): Observable<User>

    fun loadNewsFeedPage(page: Int, dateTime: DateTime?): Single<List<NewsFeedItem>>

    fun changeUserLikeForPost(feedItem: NewsFeedItem, like: Boolean): Completable

    fun answerForPoll(pollChoice: PollChoice, pollUid: String): Single<NewsFeedItem>
}