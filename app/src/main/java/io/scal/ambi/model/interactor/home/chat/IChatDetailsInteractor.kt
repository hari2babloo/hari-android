package io.scal.ambi.model.interactor.home.chat

import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.User
import io.scal.ambi.entity.chat.ChatMessage
import io.scal.ambi.entity.chat.FullChatItem

interface IChatDetailsInteractor {

    fun loadCurrentUser(): Observable<User>

    fun loadChatInfo(): Observable<FullChatItem>

    fun loadChatPage(page: Int): Single<List<ChatMessage>>
}