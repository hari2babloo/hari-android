package io.scal.ambi.model.interactor.home.chat

import android.content.Context
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Consumer
import io.scal.ambi.entity.chat.*
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.binding.observable.OptimizedObservableArrayList
import io.scal.ambi.extensions.binding.replaceElement
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.repository.data.chat.IChatRepository
import io.scal.ambi.model.repository.data.chat.data.ChatChannelChanged
import io.scal.ambi.model.repository.data.organization.IOrganizationRepository
import io.scal.ambi.model.repository.data.user.IUserRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import io.scal.ambi.ui.global.picker.FileResource
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Named


class ChatDetailsInteractor @Inject constructor(@Named("chatDescription") private val chatDescription: ChatChannelDescription,
                                                private val context: Context,
                                                private val chatRepository: IChatRepository,
                                                private val userRepository: IUserRepository,
                                                private val localUserDataRepository: ILocalUserDataRepository,
                                                private val organizationRepository: IOrganizationRepository,
                                                private val rxSchedulersAbs: RxSchedulersAbs) : IChatDetailsInteractor {

    private var currentUser: User? = null
    private val pendingMessages = OptimizedObservableArrayList<ChatMessage>()

    override fun loadCurrentUser(): Observable<User> =
        localUserDataRepository.observeCurrentUser().doOnNext { currentUser = it }

    override fun loadChatInfo(): Observable<FullChatItem> {
        return chatRepository.observeChatChangedEvents(listOf(chatDescription.uid))
            .map { it.channelInfo }
            .startWith(chatRepository.getChannelInfo(chatDescription.uid).toObservable())
            .flatMapMaybe {
                ChatInfoGenerator.generateFullChat(it, localUserDataRepository, userRepository, organizationRepository, rxSchedulersAbs)
            }
    }

    override fun loadChatPage(lastMessageIndex: Long?): Single<List<ChatMessage>> {
        return chatRepository.loadChatMessages(chatDescription.uid, lastMessageIndex)
            .flatMap {
                Observable
                    .fromIterable(it)
                    .flatMapMaybe { generateChatMessage(it, userRepository) }
                    .toList()
            }
    }

    override fun loadTypingInfo(): Observable<ChatTypingInfo> {
        return Observable.never()
    }

    override fun loadSendingMessagesInfo(): Observable<List<ChatMessage>> = pendingMessages.toObservable()

    override fun loadNewMessages(): Observable<List<ChatMessage>> {
        return chatRepository
            .observeChatChangedEvents(listOf(chatDescription.uid))
            .flatMapMaybe {
                when (it) {
                    is ChatChannelChanged.MessageAdded   -> generateChatMessage(it.message, userRepository)
                    is ChatChannelChanged.MessageUpdated -> generateChatMessage(it.message, userRepository)
                    else                                 -> Maybe.empty()
                }
                    .map { listOf(it) }
            }
    }

    override fun sendTextMessage(message: String) {
        synchronized(pendingMessages) {
            currentUser?.run {
                sendMessageInternal(
                    ChatMessage.TextMessage(UUID.randomUUID().toString(),
                                            null,
                                            this,
                                            DateTime.now(),
                                            message,
                                            emptyList(),
                                            ChatMyMessageState.PENDING),
                    null
                )
            }
        }
    }

    override fun sendPictureMessage(fileResource: FileResource) {
        synchronized(pendingMessages) {
            currentUser?.run {
                sendMessageInternal(
                    ChatMessage.AttachmentMessage(UUID.randomUUID().toString(),
                                                  null,
                                                  this,
                                                  DateTime.now(),
                                                  "",
                                                  listOf(ChatAttachment.LocalImage(fileResource)),
                                                  emptyList(),
                                                  ChatMyMessageState.PENDING),
                    null
                )
            }
        }
    }

    override fun sendFileMessage(fileResource: FileResource) {
        synchronized(pendingMessages) {
            currentUser?.run {
                sendMessageInternal(
                    ChatMessage.AttachmentMessage(UUID.randomUUID().toString(),
                                                  null,
                                                  this,
                                                  DateTime.now(),
                                                  "",
                                                  listOf(ChatAttachment.LocalFile(fileResource)),
                                                  emptyList(),
                                                  ChatMyMessageState.PENDING),
                    null
                )
            }
        }
    }

    override fun resendMessage(uid: String) {
        synchronized(pendingMessages) {
            pendingMessages
                .firstOrNull { it.uid == uid }
                ?.run {
                    if (myMessageState != ChatMyMessageState.SEND_FAILED) {
                        // we can not resend non failed message
                        return
                    }
                    val messageToResend = changeState(ChatMyMessageState.PENDING)
                    sendMessageInternal(messageToResend, this)
                }
        }
    }

    private fun sendMessageInternal(message: ChatMessage, oldMessage: ChatMessage?) {
        if (null == oldMessage) {
            pendingMessages.add(message)
        } else {
            pendingMessages.replaceElement(oldMessage, message)
        }

        executeMessageSending(message)
            .compose(rxSchedulersAbs.getIOToMainTransformerSingle())
            .subscribe(Consumer {
                synchronized(pendingMessages) {
                    if (pendingMessages.contains(message)) {
                        if (it.myMessageState == ChatMyMessageState.SEND) {
                            pendingMessages.remove(message)
                            if (message is ChatMessage.AttachmentMessage) {
                                message.attachments.mapNotNull {
                                    when (it) {
                                        is ChatAttachment.LocalImage -> it.imageFile
                                        is ChatAttachment.LocalFile  -> it.fileFile
                                        else                         -> null
                                    }
                                }.forEach { it.cleanUp() }
                            }
                        } else {
                            pendingMessages.replaceElement(message, it)
                        }
                    }
                }
            })
    }

    private fun executeMessageSending(message: ChatMessage): Single<ChatMessage> {
        val sendingSingle =
            when (message) {
                is ChatMessage.TextMessage       -> chatRepository.sendChatMessage(chatDescription.uid, message.message, null)
                is ChatMessage.AttachmentMessage -> chatRepository.sendChatMessage(chatDescription.uid,
                                                                                   message.message,
                                                                                   message.attachments.mapNotNull {
                                                                                       when (it) {
                                                                                           is ChatAttachment.LocalImage -> it.imageFile
                                                                                           is ChatAttachment.LocalFile  -> it.fileFile
                                                                                           else                         -> null
                                                                                       }
                                                                                   })
            }

        return sendingSingle
            .flatMapMaybe { generateChatMessage(it, userRepository) }
            .map { message.changeState(ChatMyMessageState.SEND) }
            .switchIfEmpty(Single.fromCallable { message.changeState(ChatMyMessageState.SEND) })
            .onErrorReturnItem(message.changeState(ChatMyMessageState.SEND_FAILED))
    }
}

private fun ChatMessage.changeState(chatMyMessageState: ChatMyMessageState): ChatMessage =
    when (this) {
        is ChatMessage.TextMessage       -> copy(myMessageState = chatMyMessageState)
        is ChatMessage.AttachmentMessage -> copy(myMessageState = chatMyMessageState)
    }