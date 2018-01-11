package io.scal.ambi.model.interactor.home.newsfeed.creation

import io.reactivex.Single
import io.scal.ambi.entity.user.User
import io.scal.ambi.model.repository.local.ILocalUserDataRepository

abstract class BaseCreationInteractor(private val localUserDataRepository: ILocalUserDataRepository) {

    fun loadAsUsers(): Single<List<User>> {
        return localUserDataRepository.observeCurrentUser()
            .map { listOf(it) }
            .firstOrError()
    }
}