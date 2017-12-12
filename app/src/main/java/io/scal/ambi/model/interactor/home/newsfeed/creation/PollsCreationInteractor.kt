package io.scal.ambi.model.interactor.home.newsfeed.creation

import io.reactivex.Completable
import io.reactivex.Single
import io.scal.ambi.entity.User
import io.scal.ambi.entity.feed.NewsFeedItemPollCreation
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import javax.inject.Inject

class PollsCreationInteractor @Inject constructor(private val localUserDataRepository: ILocalUserDataRepository) : IPollsCreationInteractor {

    override fun loadAsUsers(): Single<List<User>> {
        // todo load as users
        return localUserDataRepository.observeCurrentUser()
            .map { listOf(it) }
            .firstOrError()
    }

    override fun postPoll(newsFeedItemPollCreation: NewsFeedItemPollCreation): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}