package io.scal.ambi.model.interactor.home.newsfeed.creation

import io.reactivex.Single
import io.scal.ambi.entity.feed.AnnouncementType
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.entity.feed.NewsFeedItem
import io.scal.ambi.model.repository.data.newsfeed.IPostsRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import io.scal.ambi.model.repository.toServerResponseException
import javax.inject.Inject

class AnnouncementCreationInteractor @Inject constructor(localUserDataRepository: ILocalUserDataRepository,
                                                         private val postsRepository: IPostsRepository) :
    BaseCreationInteractor(localUserDataRepository),
    IAnnouncementCreationInteractor {

    override val availableAudiences: List<Audience> = listOf(Audience.STUDENTS, Audience.FACULTY, Audience.STAFF)
    override val availableAnnouncementTypes: List<AnnouncementType> = listOf(AnnouncementType.GENERAL,
                                                                             AnnouncementType.SAFETY,
                                                                             AnnouncementType.TRAGEDY,
                                                                             AnnouncementType.GOOD_NEWS,
                                                                             AnnouncementType.EVENT)

    override fun createAnnouncement(announcementCreation: AnnouncementCreation): Single<NewsFeedItem> {
        val audiences =
            if (announcementCreation.audiences == Audience.EVERYONE) {
                availableAudiences
            } else {
                listOf(announcementCreation.audiences)
            }

        return postsRepository
            .postNewAnnouncement(announcementCreation.pinned,
                                 announcementCreation.locked,
                                 announcementCreation.selectedAsUser.uid,
                                 announcementCreation.announcementText,
                                 announcementCreation.announcementType,
                                 audiences
            )
            .onErrorResumeNext { t -> Single.error(t.toServerResponseException()) }
    }
}