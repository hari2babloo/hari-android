package io.scal.ambi.model.interactor.profile

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.actions.Comment
import io.scal.ambi.entity.feed.NewsFeedItem
import io.scal.ambi.entity.feed.NewsFeedItemPoll
import io.scal.ambi.entity.feed.PollChoice
import io.scal.ambi.entity.user.User
import io.scal.ambi.ui.global.picker.FileResource

interface IProfileDetailsInteractor {

    fun loadCurrentUser(): Observable<User>

    fun loadUser(profileUid: String): Observable<User>

    fun loadNewsFeedPage(entityType: String,currentUser: Boolean, profileUid: String, page: Int): Single<List<NewsFeedItem>>

    fun changeUserLikeForPost(feedItem: NewsFeedItem, like: Boolean): Completable

    fun answerForPoll(feedItemPoll: NewsFeedItemPoll, pollChoice: PollChoice): Single<NewsFeedItem>

    fun sendUserCommentToPost(newsFeedItem: NewsFeedItem, userCommentText: String): Single<Comment>

    fun attachAvatarImage(fileResource: FileResource): Single<User>

    fun attachBannerImage(fileResource: FileResource): Single<User>
}