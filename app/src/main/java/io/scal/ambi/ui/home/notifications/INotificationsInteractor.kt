package io.scal.ambi.ui.home.notifications

import io.reactivex.Single

/**
 * Created by chandra on 30-07-2018.
 */
interface INotificationsInteractor {

    fun loadNotifications(page: Int): Single<List<NotificationData>>

}