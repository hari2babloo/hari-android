package io.scal.ambi.model.interactor.home.chat

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Function3
import io.scal.ambi.entity.chat.ChatChannelDescription
import io.scal.ambi.entity.chat.ChatMessage
import io.scal.ambi.entity.chat.PreviewChatItem
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.model.data.server.ServerResponseException
import io.scal.ambi.model.repository.data.chat.IChatRepository
import io.scal.ambi.model.repository.data.chat.data.ChatChannelInfo
import io.scal.ambi.model.repository.data.chat.data.ChatChannelMessage
import io.scal.ambi.model.repository.data.chat.data.ChatClientChanged
import io.scal.ambi.model.repository.data.user.IUserRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import javax.inject.Inject

class ChatListInteractor @Inject constructor(private val localUserDataRepository: ILocalUserDataRepository,
                                             private val chatRepository: IChatRepository,
                                             private val userRepository: IUserRepository,
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

    private fun generateChatItem(chatInfo: ChatChannelInfo): Maybe<PreviewChatItem>? {
        return generateChatUsers(chatInfo)
            .flatMapMaybe { users ->
                if (checkChatInfo(users)) {
                    Observable
                        .combineLatest(
                            generateChatIcon(chatInfo, users, localUserDataRepository.getCurrentUser()).toObservable(),
                            generateChatMessage(chatInfo.lastMessage).map { it as Any }.toSingle(Unit).toObservable(),
                            generateChatName(chatInfo, users, localUserDataRepository.getCurrentUser()).toObservable(),
                            Function3<Any, Any, Any, Array<Any>> { t1, t2, t3 -> arrayOf(t1, t2, t3) }
                        )
                        .firstOrError()
                        .map { array ->
                            generateChatItem(chatInfo, users, array[0] as IconImage, array[1] as? ChatMessage, array[2] as String)
                        }
                        .toMaybe()
                } else {
                    Maybe.empty<PreviewChatItem>()
                }
            }
    }

    private fun generateChatItem(chatInfo: ChatChannelInfo,
                                 users: List<User>,
                                 icon: IconImage,
                                 lastMessage: ChatMessage?,
                                 name: String): PreviewChatItem {
        val description = ChatChannelDescription(chatInfo.uid, name, icon, chatInfo.dateTime)
        return when (chatInfo.type) {
            ChatChannelInfo.Type.SIMPLE    -> PreviewChatItem.Direct(description,
                                                                     icon,
                                                                     users,
                                                                     lastMessage,
                                                                     chatInfo.hasNewMessages)
            ChatChannelInfo.Type.ORG_GROUP -> PreviewChatItem.Group(description,
                                                                    listOf(description),
                                                                    icon,
                                                                    users,
                                                                    lastMessage,
                                                                    chatInfo.hasNewMessages)
            ChatChannelInfo.Type.ORG_CLASS -> PreviewChatItem.Group(description,
                                                                    listOf(description),
                                                                    icon,
                                                                    users,
                                                                    lastMessage,
                                                                    chatInfo.hasNewMessages)
        }
    }

    private fun checkChatInfo(users: List<User>): Boolean =
        when {
            users.isEmpty() -> false
            else            -> true
        }

    private fun generateChatUsers(chatInfo: ChatChannelInfo): Single<List<User>> =
        Observable.fromIterable(chatInfo.memberUids)
            .flatMapMaybe { getUserProfile(it) }
            .toList()

    private fun generateChatMessage(message: ChatChannelMessage?): Maybe<ChatMessage> {
        if (null == message) {
            return Maybe.empty()
        }
        return getUserProfile(message.sender)
            .map { sender ->
                if (null == message.media) {
                    ChatMessage.TextMessage(message.uid, sender, message.sendDate, message.message)
                } else {
                    ChatMessage.AttachmentMessage(message.uid, sender, message.sendDate, message.message, listOf(message.media.toAttachment()))
                }
            }
    }

    private fun getUserProfile(uid: String): Maybe<User> =
        userRepository.getProfileCached(uid).toMaybe().onErrorComplete { it is ServerResponseException && it.notFound && it.serverError }
}

class PageResult(val hasMore: Boolean, val data: List<PreviewChatItem> = emptyList())