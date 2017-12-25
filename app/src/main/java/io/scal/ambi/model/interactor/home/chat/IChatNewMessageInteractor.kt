package io.scal.ambi.model.interactor.home.chat

import io.reactivex.Single
import io.scal.ambi.entity.user.User

interface IChatNewMessageInteractor {

    fun loadUserWithPrefix(searchString: String, page: Int): Single<List<User>>
}