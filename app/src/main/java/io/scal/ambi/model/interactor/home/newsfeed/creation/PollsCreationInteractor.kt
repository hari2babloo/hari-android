package io.scal.ambi.model.interactor.home.newsfeed.creation

import io.reactivex.Single
import io.scal.ambi.entity.User
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import javax.inject.Inject

class PollsCreationInteractor @Inject constructor(private val localUserDataRepository: ILocalUserDataRepository) : IPollsCreationInteractor {

    override fun loadAsUsers(): Single<List<User>> {
        // todo load as users
        return localUserDataRepository.observeCurrentUser()
            .map { listOf(it) }
            .firstOrError()
    }
}