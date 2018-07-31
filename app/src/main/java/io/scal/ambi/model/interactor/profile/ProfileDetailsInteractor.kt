package io.scal.ambi.model.interactor.profile

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.actions.Comment
import io.scal.ambi.entity.feed.NewsFeedItem
import io.scal.ambi.entity.feed.NewsFeedItemPoll
import io.scal.ambi.entity.feed.PollChoice
import io.scal.ambi.entity.user.User
import io.scal.ambi.model.interactor.base.IFileUploadInteractor
import io.scal.ambi.model.repository.data.newsfeed.IPostsRepository
import io.scal.ambi.model.repository.data.user.IUserRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import io.scal.ambi.ui.global.picker.FileResource
import javax.inject.Inject

class ProfileDetailsInteractor @Inject constructor(private val postsRepository: IPostsRepository,
                                                   private val localUserDataRepository: ILocalUserDataRepository,
                                                   private val userRepository: IUserRepository,
                                                   private val fileUploadInteractor: IFileUploadInteractor) : IProfileDetailsInteractor {

    override fun loadCurrentUser(): Observable<User> =
        localUserDataRepository.observeCurrentUser()

    override fun loadUser(profileUid: String): Observable<User> =
        userRepository.getProfile(profileUid).toObservable()

    override fun loadNewsFeedPage(entityType: String,currentUser: Boolean, profileUid: String, page: Int): Single<List<NewsFeedItem>> =
        if (currentUser) {
            postsRepository.loadPostsPersonal(entityType,page.toLong() - 1)
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

    override fun attachAvatarImage(fileResource: FileResource): Single<User> {
        return localUserDataRepository.observeCurrentUser()
            .firstOrError()
            .flatMap { currentUser ->
                fileUploadInteractor.uploadImage(fileResource, currentUser)
                    .flatMap { userRepository.saveProfileAvatar(currentUser.uid, it.id) }
            }
    }

    override fun attachBannerImage(fileResource: FileResource): Single<User> {
        return localUserDataRepository.observeCurrentUser()
            .firstOrError()
            .flatMap { currentUser ->
                fileUploadInteractor.uploadImage(fileResource, currentUser, null)
                    .flatMap { userRepository.saveProfileBanner(currentUser.uid, it.id) }
            }
    }
}