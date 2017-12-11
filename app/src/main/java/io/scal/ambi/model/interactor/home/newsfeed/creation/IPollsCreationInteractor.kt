package io.scal.ambi.model.interactor.home.newsfeed.creation

import io.reactivex.Single
import io.scal.ambi.entity.User

interface IPollsCreationInteractor {

    fun loadAsUsers(): Single<List<User>>
}