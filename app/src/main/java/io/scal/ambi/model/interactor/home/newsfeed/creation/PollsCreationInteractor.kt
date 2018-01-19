package io.scal.ambi.model.interactor.home.newsfeed.creation

import io.reactivex.Completable
import io.scal.ambi.entity.feed.PollEndsTime
import io.scal.ambi.model.repository.data.newsfeed.IPostsRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import io.scal.ambi.model.repository.toServerResponseException
import org.joda.time.DateTime
import javax.inject.Inject

class PollsCreationInteractor @Inject constructor(localUserDataRepository: ILocalUserDataRepository,
                                                  private val postsRepository: IPostsRepository) :
    BaseCreationInteractor(localUserDataRepository),
    IPollsCreationInteractor {

    override fun postPoll(pollCreation: PollCreation): Completable {
        val duration =
            if (pollCreation.pollDuration is PollEndsTime.Never) {
                null
            } else {
                (pollCreation.pollDuration as PollEndsTime.TimeDuration).duration
            }

        return postsRepository
            .postNewPoll(pollCreation.pinned,
                         pollCreation.locked,
                         pollCreation.selectedAsUser.uid,
                         pollCreation.questionText,
                         pollCreation.choices.map { it.text },
                         duration?.let { DateTime.now().plus(it) }
            )
            .onErrorResumeNext { t -> Completable.error(t.toServerResponseException()) }
    }
}