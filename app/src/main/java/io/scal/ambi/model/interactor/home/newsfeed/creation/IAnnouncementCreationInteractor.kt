package io.scal.ambi.model.interactor.home.newsfeed.creation

import io.reactivex.Completable
import io.reactivex.Single
import io.scal.ambi.entity.feed.AnnouncementType
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.entity.user.User

interface IAnnouncementCreationInteractor {

    val availableAudiences: List<Audience>
    val availableAnnouncementTypes: List<AnnouncementType>

    fun loadAsUsers(): Single<List<User>>

    fun createAnnouncement(announcementCreation: AnnouncementCreation): Completable
}