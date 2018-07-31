package io.scal.ambi.ui.home.notifications

import io.reactivex.Single
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.entity.feed.NewsFeedItem
import io.scal.ambi.model.interactor.home.newsfeed.INewsFeedInteractor
import io.scal.ambi.model.repository.data.newsfeed.IPostsRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import javax.inject.Inject

class NotificationInteractor @Inject constructor(private val postsRepository: IPostsRepository,
                                                 private val localUserDataRepository: ILocalUserDataRepository) : INotificationsInteractor {
    override fun loadNotifications(page: Int): Single<List<NotificationData>> {
        return postsRepository.loadNotification(page)
    }

}