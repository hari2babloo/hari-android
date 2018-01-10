package io.scal.ambi.model.interactor.home.newsfeed.creation

import io.reactivex.Single
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.entity.feed.NewsFeedItem
import io.scal.ambi.model.repository.data.newsfeed.IPostsRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import io.scal.ambi.model.repository.toServerResponseException
import javax.inject.Inject

class StatusUpdateInteractor @Inject constructor(localUserDataRepository: ILocalUserDataRepository,
                                                 private val postsRepository: IPostsRepository) :
    BaseCreationInteractor(localUserDataRepository),
    IStatusUpdateInteractor {

    override val availableAudiences: List<Audience> = listOf(Audience.STUDENTS, Audience.FACULTY, Audience.STAFF)

    override fun updateStatus(statusUpdate: StatusUpdate): Single<NewsFeedItem> {
        val audiences =
            if (statusUpdate.audiences == Audience.EVERYONE) {
                availableAudiences
            } else {
                listOf(statusUpdate.audiences)
            }

        return postsRepository
            .postNewStatus(statusUpdate.pinned,
                           statusUpdate.locked,
                           statusUpdate.selectedAsUser.uid,
                           statusUpdate.statusText,
                           audiences
            )
            .onErrorResumeNext { t -> Single.error(t.toServerResponseException()) }
    }
}