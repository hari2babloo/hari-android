package io.scal.ambi.model.interactor.home.newsfeed

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.actions.Comment
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.entity.feed.NewsFeedItem
import io.scal.ambi.entity.feed.NewsFeedItemPoll
import io.scal.ambi.entity.feed.PollChoice
import io.scal.ambi.entity.user.User
import io.scal.ambi.model.repository.data.newsfeed.IPostsRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import javax.inject.Inject

class NewsFeedInteractor @Inject constructor(private val postsRepository: IPostsRepository,
                                             private val localUserDataRepository: ILocalUserDataRepository) : INewsFeedInteractor {

    override fun loadCurrentUser(): Observable<User> =
        localUserDataRepository.observeCurrentUser()

    override fun loadNewsFeedPage(page: Int, audience: Audience): Single<List<NewsFeedItem>> =
        postsRepository.loadPostsGeneral(page.toLong() - 1, audience)

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