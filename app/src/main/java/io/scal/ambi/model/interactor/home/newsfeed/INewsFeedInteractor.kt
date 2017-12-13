package io.scal.ambi.model.interactor.home.newsfeed

import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.User
import io.scal.ambi.entity.feed.NewsFeedItem
import io.scal.ambi.entity.feed.PollChoice
import org.joda.time.LocalDateTime

interface INewsFeedInteractor {

    fun loadNewsFeedPage(page: Int, dateTime: LocalDateTime?): Single<List<NewsFeedItem>>

    fun loadCurrentUser(): Observable<User>

    fun answerForPoll(pollChoice: PollChoice, pollUid: String): Single<NewsFeedItem>
}