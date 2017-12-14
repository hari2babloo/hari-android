package io.scal.ambi.model.interactor.home.chat

import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.User
import io.scal.ambi.entity.chat.SmallChatItem

interface IChatListInteractor {

    fun loadCurrentUser(): Observable<User>

    fun loadChatListPage(page: Int): Single<List<SmallChatItem>>
}