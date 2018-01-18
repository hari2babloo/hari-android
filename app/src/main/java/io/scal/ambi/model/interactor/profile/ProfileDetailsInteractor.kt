package io.scal.ambi.model.interactor.profile

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.actions.Comment
import io.scal.ambi.entity.feed.NewsFeedItem
import io.scal.ambi.entity.feed.NewsFeedItemPoll
import io.scal.ambi.entity.feed.PollChoice
import io.scal.ambi.entity.user.User
import io.scal.ambi.model.repository.data.newsfeed.IPostsRepository
import io.scal.ambi.model.repository.data.user.IUserRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import javax.inject.Inject

class ProfileDetailsInteractor @Inject constructor(private val postsRepository: IPostsRepository,
                                                   private val localUserDataRepository: ILocalUserDataRepository,
                                                   private val userRepository: IUserRepository) : IProfileDetailsInteractor {

    override fun loadCurrentUser(): Observable<User> =
        localUserDataRepository.observeCurrentUser()

    override fun loadUser(profileUid: String): Observable<User> =
        userRepository.getProfile(profileUid).toObservable()

    override fun loadNewsFeedPage(currentUser: Boolean, profileUid: String, page: Int): Single<List<NewsFeedItem>> =
        if (currentUser) {
            postsRepository.loadPostsPersonal(page.toLong() - 1)
        } else {
            postsRepository.loadPostsForUser(profileUid, page.toLong() - 1)
        }

    override fun changeUserLikeForPost(feedItem: NewsFeedItem, like: Boolean): Completable {
        return postsRepository.changeUserLikeForPost(feedItem, like)
    }

    override fun answerForPoll(feedItemPoll: NewsFeedItemPoll, pollChoice: PollChoice): Single<NewsFeedItem> {
        return postsRepository.answerForPoll(feedItemPoll, pollChoice)
    }

    override fun sendUserCommentToPost(newsFeedItem: NewsFeedItem, userCommentText: String): Single<Comment> {
        return postsRepository.sendUserCommentToPost(newsFeedItem, userCommentText)
    }
}