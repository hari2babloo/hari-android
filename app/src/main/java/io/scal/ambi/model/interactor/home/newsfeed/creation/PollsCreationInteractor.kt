package io.scal.ambi.model.interactor.home.newsfeed.creation

import io.reactivex.Completable
import io.reactivex.Single
import io.scal.ambi.entity.User
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.entity.feed.NewsFeedItemPollCreation
import io.scal.ambi.entity.feed.PollsEndsTime
import io.scal.ambi.model.repository.data.newsfeed.IPostsRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import io.scal.ambi.model.repository.toServerResponseException
import javax.inject.Inject

class PollsCreationInteractor @Inject constructor(private val localUserDataRepository: ILocalUserDataRepository,
                                                  private val postsRepository: IPostsRepository) : IPollsCreationInteractor {

    override val availableAudiences: List<Audience> = listOf(Audience.STUDENTS, Audience.FACULTY, Audience.STAFF)

    override fun loadAsUsers(): Single<List<User>> {
        // todo load as users
        return localUserDataRepository.observeCurrentUser()
            .map { listOf(it) }
            .firstOrError()
    }

    override fun postPoll(newsFeedItemPollCreation: NewsFeedItemPollCreation): Completable {
        val duration =
            if (newsFeedItemPollCreation.selectedPollDuration is PollsEndsTime.Never) {
                null
            } else {
                (newsFeedItemPollCreation.selectedPollDuration as PollsEndsTime.TimeDuration).duration
            }
        val audiences =
            if (newsFeedItemPollCreation.selectedAudience == Audience.EVERYONE) {
                availableAudiences
            } else {
                listOf(newsFeedItemPollCreation.selectedAudience)
            }

        return postsRepository
            .postNewPoll(newsFeedItemPollCreation.pinned,
                         newsFeedItemPollCreation.locked,
                         newsFeedItemPollCreation.selectedAsUser.uid,
                         newsFeedItemPollCreation.questionText,
                         newsFeedItemPollCreation.choices,
                         duration,
                         audiences
            )
            .onErrorResumeNext { t -> Completable.error(t.toServerResponseException()) }
    }
}