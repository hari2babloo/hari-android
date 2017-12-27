package io.scal.ambi.model.interactor.home.chat

import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.user.User
import io.scal.ambi.entity.chat.PreviewChatItem

interface IChatListInteractor {

    fun loadCurrentUser(): Observable<User>

    fun loadChatListPage(page: Int): Single<List<PreviewChatItem>>
}