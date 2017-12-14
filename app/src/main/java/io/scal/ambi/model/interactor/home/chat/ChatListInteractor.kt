package io.scal.ambi.model.interactor.home.chat

import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.User
import io.scal.ambi.entity.chat.SmallChatItem
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import javax.inject.Inject

class ChatListInteractor @Inject constructor(private val localUserDataRepository: ILocalUserDataRepository) : IChatListInteractor {

    override fun loadCurrentUser(): Observable<User> =
        localUserDataRepository.observeCurrentUser()
            .onErrorResumeNext(Observable.never<User>())

    override fun loadChatListPage(page: Int): Single<List<SmallChatItem>> {
        return Single.error(NotImplementedError("not implemented"))
    }
}