package io.scal.ambi.model.interactor.home.newsfeed.creation

import io.reactivex.Single
import io.scal.ambi.entity.user.User
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.entity.feed.NewsFeedItem
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

    override fun postPoll(pollCreation: PollCreation): Single<NewsFeedItem> {
        val duration =
            if (pollCreation.pollDuration is PollEndsTime.Never) {
                null
            } else {
                (pollCreation.pollDuration as PollEndsTime.TimeDuration).duration
            }
        val audiences =
            if (pollCreation.audiences == Audience.EVERYONE) {
                availableAudiences
            } else {
                listOf(pollCreation.audiences)
            }

        return postsRepository
            .postNewPoll(pollCreation.pinned,
                         pollCreation.locked,
                         pollCreation.selectedAsUser.uid,
                         pollCreation.questionText,
                         pollCreation.choices.map { it.text },
                         duration,
                         audiences,
                         listOf(IPostsRepository.Host(pollCreation.selectedAsUser.uid, PostHostKind.USER))
            )
            .onErrorResumeNext { t -> Single.error(t.toServerResponseException()) }
    }
}