package io.scal.ambi.ui.home.notifications

import io.reactivex.Single
import io.scal.ambi.model.repository.data.newsfeed.IPostsRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import javax.inject.Inject

class NotificationInteractor @Inject constructor(private val postsRepository: IPostsRepository,
                                                 private val localUserDataRepository: ILocalUserDataRepository) : INotificationsInteractor {
    override fun loadNotifications(): Single<List<NotificationData>> {
        return postsRepository.loadNotification()
    }

    /*override fun loadNotifications(page: Int, category: String): Single<List<NotificationData>> {
        return postsRepository.loadNotification(page,category)
    }*/

}