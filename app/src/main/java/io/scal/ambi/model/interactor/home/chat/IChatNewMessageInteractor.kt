package io.scal.ambi.model.interactor.home.chat

import io.reactivex.Single
import io.scal.ambi.entity.chat.PreviewChatItem
import io.scal.ambi.entity.user.User

interface IChatNewMessageInteractor {

    fun loadUserWithPrefix(userSelectionText: String, page: Int): Single<List<User>>

    fun createChat(selectedUsers: List<User>): Single<PreviewChatItem>
}