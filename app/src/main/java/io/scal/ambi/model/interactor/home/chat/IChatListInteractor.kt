package io.scal.ambi.model.interactor.home.chat

import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.chat.PreviewChatItem
import io.scal.ambi.entity.user.User

interface IChatListInteractor {

    fun loadCurrentUser(): Observable<User>

    fun loadChatListPage(firstPage: Boolean): Single<List<PreviewChatItem>>

    fun observeRuntimeDataChanges(): Observable<PreviewChatItem>

    fun observeRuntimeDataChangesForChats(chatUids: List<String>): Observable<PreviewChatItem>
}