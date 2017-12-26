package io.scal.ambi.model.interactor.home.chat

import io.reactivex.Single
import io.scal.ambi.entity.chat.SmallChatItem
import io.scal.ambi.entity.user.User

interface IChatNewMessageInteractor {

    fun loadUserWithPrefix(): Single<List<User>>

    fun createChat(selectedUsers: List<User>): Single<SmallChatItem>
}