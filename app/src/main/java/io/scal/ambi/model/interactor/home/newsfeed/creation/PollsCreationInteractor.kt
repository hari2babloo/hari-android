package io.scal.ambi.model.interactor.home.newsfeed.creation

import io.reactivex.Completable
import io.reactivex.Single
import io.scal.ambi.entity.User
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.entity.feed.NewsFeedItemPoll
import io.scal.ambi.entity.feed.PollEndsTime
import io.scal.ambi.model.repository.data.newsfeed.IPostsRepository
import io.scal.ambi.model.repository.data.newsfeed.PostHostKind
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

    override fun postPoll(newsFeedItemPoll: NewsFeedItemPoll): Completable {
        val duration =
            if (newsFeedItemPoll.selectedPollDuration is PollEndsTime.Never) {
                null
            } else {
                (newsFeedItemPoll.selectedPollDuration as PollEndsTime.TimeDuration).duration
            }
        val audiences =
            if (newsFeedItemPoll.selectedAudience == Audience.EVERYONE) {
                availableAudiences
            } else {
                listOf(newsFeedItemPoll.selectedAudience)
            }

        return localUserDataRepository.observeCurrentUser()
            .firstOrError()
            .flatMapCompletable { user ->
                postsRepository
                    .postNewPoll(newsFeedItemPoll.pinned,
                                 newsFeedItemPoll.locked,
                                 newsFeedItemPoll.selectedAsUser.uid,
                                 newsFeedItemPoll.questionText,
                                 newsFeedItemPoll.choices.map { it.text },
                                 duration,
                                 audiences,
                                 listOf(IPostsRepository.Host(user.uid, PostHostKind.USER))
                    )
                    .onErrorResumeNext { t -> Completable.error(t.toServerResponseException()) }
            }
    }
}