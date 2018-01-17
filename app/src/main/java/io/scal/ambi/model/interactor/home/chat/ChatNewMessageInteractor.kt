package io.scal.ambi.model.interactor.home.chat

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.scal.ambi.entity.chat.PreviewChatItem
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.repository.data.chat.IChatRepository
import io.scal.ambi.model.repository.data.organization.IOrganizationRepository
import io.scal.ambi.model.repository.data.user.IUserRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import io.scal.ambi.ui.home.chat.newmessage.AppendingData
import java.util.concurrent.Executors
import javax.inject.Inject

class ChatNewMessageInteractor @Inject constructor(private val appendingData: AppendingData,
                                                   private val userRepository: IUserRepository,
                                                   private val chatRepository: IChatRepository,
                                                   private val localUserDataRepository: ILocalUserDataRepository,
                                                   private val organizationRepository: IOrganizationRepository,
                                                   private val rxSchedulersAbs: RxSchedulersAbs) : IChatNewMessageInteractor {

    private val scheduler = Schedulers.from(Executors.newSingleThreadExecutor())

    private val allUsers = mutableListOf<User>()

    override fun loadUserWithPrefix(userSelectionText: String, page: Int): Single<List<User>> {
        return when {
            page > 1                    -> Single.just(emptyList()) // we don't support pagination now
            userSelectionText.isEmpty() -> Single.just(emptyList<User>()).flatMap { generateFullList(it) } // we don't support empty data search
            else                        -> userRepository.searchProfiles(userSelectionText).flatMap { generateFullList(it) }
        }
    }

    private fun generateFullList(users: List<User>): Single<List<User>> {
        return Single.just(users)
            .observeOn(rxSchedulersAbs.computationScheduler)
            .flatMap {
                if (appendingData is AppendingData.Data) {
                    Observable.fromIterable(it)
                        .filter { (appendingData as? AppendingData.Data)?.currentMemebers?.contains(it) != true }
                        .toList()
                } else {
                    Single.just(it)
                }
            }
            .flatMap {
                val currentUser = localUserDataRepository.getCurrentUser()
                Observable.fromIterable(it)
                    .filter { it != currentUser }
                    .toList()
            }
            .observeOn(scheduler)
            .map { newUsers ->
                newUsers.filter { !allUsers.contains(it) }.forEach { allUsers.add(it) }
                allUsers.toMutableList() as List<User>
            }
            .observeOn(rxSchedulersAbs.computationScheduler)
    }

    override fun createChat(selectedUsers: List<User>): Single<PreviewChatItem> {
        val currentUser = localUserDataRepository.getCurrentUser()
            ?: return Single.error(IllegalStateException("not able to create channel without authentication"))
        val userData =
            if (appendingData is AppendingData.Data) {
                selectedUsers.plus(appendingData.currentMemebers)
            } else {
                selectedUsers
            }
        return chatRepository
            .createChat(IChatRepository.ChatCreateInfo(null, null, userData.filter { it != currentUser }.map { it.uid }), currentUser.uid)
            .flatMap { chatInfo ->
                ChatInfoGenerator.generatePreviewChat(chatInfo, localUserDataRepository, userRepository, organizationRepository, rxSchedulersAbs)
                    .toSingle()
            }
    }
}