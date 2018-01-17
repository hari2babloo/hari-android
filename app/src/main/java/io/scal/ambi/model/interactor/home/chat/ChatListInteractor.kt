package io.scal.ambi.model.interactor.home.chat

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.chat.PreviewChatItem
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.repository.data.chat.IChatRepository
import io.scal.ambi.model.repository.data.chat.data.ChatChannelChanged
import io.scal.ambi.model.repository.data.chat.data.ChatChannelInfo
import io.scal.ambi.model.repository.data.chat.data.ChatClientChanged
import io.scal.ambi.model.repository.data.organization.IOrganizationRepository
import io.scal.ambi.model.repository.data.user.IUserRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import javax.inject.Inject

class ChatListInteractor @Inject constructor(private val localUserDataRepository: ILocalUserDataRepository,
                                             private val chatRepository: IChatRepository,
                                             private val userRepository: IUserRepository,
                                             private val organizationRepository: IOrganizationRepository,
                                             private val rxSchedulersAbs: RxSchedulersAbs) : IChatListInteractor {
    private var currentPage = 0

    override fun loadCurrentUser(): Observable<User> =
        localUserDataRepository.observeCurrentUser()

    override fun loadChatListPage(firstPage: Boolean): Single<List<PreviewChatItem>> =
        if (firstPage) {
            loadChatListPageWithSkipLogic(0)
        } else {
            loadChatListPageWithSkipLogic(currentPage + 1)
        }

    override fun observeRuntimeDataChanges(): Observable<PreviewChatItem> {
        return chatRepository.observeChatClientChanged()
            .observeOn(rxSchedulersAbs.ioScheduler)
            .flatMapMaybe {
                when (it) {
                    is ChatClientChanged.ChatAdded   -> generateChatItem(it.channelInfo)
                    is ChatClientChanged.ChatUpdated -> generateChatItem(it.channelInfo)
                    else                             -> Maybe.empty()
                }
            }
    }

    override fun observeRuntimeDataChangesForChats(chatUids: List<String>): Observable<PreviewChatItem> {
        return chatRepository.observeChatChangedEvents(chatUids)
            .observeOn(rxSchedulersAbs.ioScheduler)
            .flatMapMaybe {
                when (it) {
                    is ChatChannelChanged.MessageAdded   -> generateChatItem(it.channelInfo)
                    is ChatChannelChanged.MessageUpdated -> generateChatItem(it.channelInfo)
                    is ChatChannelChanged.MessageRemoved -> generateChatItem(it.channelInfo)
                    is ChatChannelChanged.MemberAdded    -> generateChatItem(it.channelInfo)
                    is ChatChannelChanged.MemberRemoved  -> generateChatItem(it.channelInfo)
                    else                                 -> Maybe.empty()
                }
            }
    }

    private fun loadChatListPageWithSkipLogic(page: Int): Single<List<PreviewChatItem>> {
        return loadChatListPageInner(page)
            .flatMap {
                when {
                    it.data.isNotEmpty() -> Single.just(it.data)
                    it.hasMore           -> loadChatListPageWithSkipLogic(page + 1)
                    else                 -> Single.just(emptyList())
                }
            }
            .doOnSuccess { currentPage = page }
    }

    private fun loadChatListPageInner(page: Int): Single<PageResult> {
        return chatRepository.loadAllChannelList(page)
            .observeOn(rxSchedulersAbs.ioScheduler)
            .flatMap {
                if (it.isEmpty()) {
                    Single.just(PageResult(false))
                } else {
                    Observable.fromIterable(it)
                        .flatMapMaybe { chatInfo -> generateChatItem(chatInfo) }
                        .toList()
                        .map { PageResult(true, it) }
                }
            }
    }

    private fun generateChatItem(chatChannelInfo: ChatChannelInfo): Maybe<PreviewChatItem> {
        return ChatInfoGenerator.generatePreviewChat(chatChannelInfo,
                                                     localUserDataRepository,
                                                     userRepository,
                                                     organizationRepository,
                                                     rxSchedulersAbs)
    }
}

class PageResult(val hasMore: Boolean, val data: List<PreviewChatItem> = emptyList())