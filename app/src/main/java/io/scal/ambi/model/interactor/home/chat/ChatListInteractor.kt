package io.scal.ambi.model.interactor.home.chat

import android.os.SystemClock
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.scal.ambi.entity.chat.ChatChannelDescription
import io.scal.ambi.entity.chat.ChatMessage
import io.scal.ambi.entity.chat.PreviewChatItem
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.IconImageUser
import io.scal.ambi.model.data.server.ServerResponseException
import io.scal.ambi.model.repository.data.chat.ChatChannelInfo
import io.scal.ambi.model.repository.data.chat.ChatChannelMessage
import io.scal.ambi.model.repository.data.chat.IChatRepository
import io.scal.ambi.model.repository.data.user.IUserRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import java.util.*
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
                        .flatMapMaybe { chatInfo ->
                            generateChatUsers(chatInfo)
                                .flatMapMaybe { users ->
                                    if (checkChatInfo(chatInfo, users)) {
                                        Observable
                                            .combineLatest(
                                                generateChatIcon(users).toObservable(),
                                                generateChatMessage(chatInfo.lastMessage).map { it as Any }.toSingle(Unit).toObservable(),
                                                BiFunction<Any, Any, Array<Any>> { t1, t2 -> arrayOf(t1, t2) }
                                            )
                                            .firstOrError()
                                            .map { array ->
                                                val icon = array[0] as IconImage
                                                val lastMessage = array[2] as? ChatMessage

                                                // sometimes twilio has bad chats. so we should filter them
                                                val name =
                                                    chatInfo.name ?:
                                                        users.map { it.name }.fold("", { acc, name -> if (acc.isEmpty()) name else "$acc , $name" })

                                                val description = ChatChannelDescription(chatInfo.uid, name, icon, chatInfo.dateTime)
                                                val item =
                                                    when (chatInfo.type) {
                                                        ChatChannelInfo.Type.DIRECT -> PreviewChatItem.Direct(description,
                                                                                                              users[0],
                                                                                                              lastMessage,
                                                                                                              chatInfo.hasNewMessages)
                                                        ChatChannelInfo.Type.GROUP  -> PreviewChatItem.Group(description,
                                                                                                             listOf(description),
                                                                                                             icon,
                                                                                                             users,
                                                                                                             lastMessage,
                                                                                                             chatInfo.hasNewMessages)
                                                        ChatChannelInfo.Type.CLASS  -> PreviewChatItem.Group(description,
                                                                                                             listOf(description),
                                                                                                             icon,
                                                                                                             users,
                                                                                                             lastMessage,
                                                                                                             chatInfo.hasNewMessages)
                                                    }
                                                item
                                            }
                                            .toMaybe()
                                    } else {
                                        Maybe.empty<PreviewChatItem>()
                                    }
                                }
                        }
                        .toList()
                        .map { PageResult(true, it) }
                }
            }
    }

    private fun checkChatInfo(chatInfo: ChatChannelInfo, users: List<User>): Boolean =
        when {
            users.isEmpty()                                                 -> false
            chatInfo.type == ChatChannelInfo.Type.DIRECT && users.size != 2 -> false
            else                                                            -> true
        }

    private fun generateChatIcon(members: List<User>): Single<IconImage> =
        if (members.isEmpty()) {
            Maybe.empty()
        } else {
            val member = members[Random(SystemClock.currentThreadTimeMillis()).nextInt(members.size)]
            Maybe.just(member.avatar as IconImage)
        }
            .toSingle(IconImageUser())

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
        userRepository.getProfile(uid).toMaybe().onErrorComplete { it is ServerResponseException && it.notFound && it.serverError }
}

class PageResult(val hasMore: Boolean, val data: List<PreviewChatItem> = emptyList())