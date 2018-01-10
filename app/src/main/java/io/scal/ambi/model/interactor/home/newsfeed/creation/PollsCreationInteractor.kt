package io.scal.ambi.model.interactor.home.newsfeed.creation

import io.reactivex.Single
import io.scal.ambi.entity.feed.NewsFeedItem
import io.scal.ambi.entity.feed.PollEndsTime
import io.scal.ambi.model.repository.data.newsfeed.IPostsRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import io.scal.ambi.model.repository.toServerResponseException
import javax.inject.Inject

class PollsCreationInteractor @Inject constructor(localUserDataRepository: ILocalUserDataRepository,
                                                  private val postsRepository: IPostsRepository) :
    BaseCreationInteractor(localUserDataRepository),
    IPollsCreationInteractor {

    override fun postPoll(pollCreation: PollCreation): Single<NewsFeedItem> {
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
                         duration
            )
            .onErrorResumeNext { t -> Single.error(t.toServerResponseException()) }
    }
}