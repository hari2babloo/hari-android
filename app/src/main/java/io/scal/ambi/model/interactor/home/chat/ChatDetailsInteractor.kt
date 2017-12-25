package io.scal.ambi.model.interactor.home.chat

import android.content.Context
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.R
import io.scal.ambi.entity.chat.*
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.binding.binders.toFrescoImagePath
import io.scal.ambi.extensions.binding.observable.OptimizedObservableArrayList
import io.scal.ambi.extensions.binding.replaceElement
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.IconImageUser
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import io.scal.ambi.ui.global.picker.FileResource
import org.joda.time.DateTime
import org.joda.time.Duration
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named


class ChatDetailsInteractor @Inject constructor(@Named("chatUid") private val chatUid: String,
                                                private val context: Context,
                                                private val localUserDataRepository: ILocalUserDataRepository,
                                                private val rxSchedulersAbs: RxSchedulersAbs) : IChatDetailsInteractor {

    private var currentUser: User? = null
    private val pendingMessages = OptimizedObservableArrayList<ChatMessage>()

    override fun loadCurrentUser(): Observable<User> =
        localUserDataRepository.observeCurrentUser().doOnNext { currentUser = it }

    override fun loadChatInfo(): Observable<FullChatItem> {
        if (SecureRandom().nextBoolean()) {
            return Observable.just(FullChatItem.Direct("fasf",
                                                       DateTime.now().minus(Duration.standardHours(3)),
                                                       User.asStudent("1",
                                                                      IconImageUser("http://cdn01.ru/files/users/images/32/c4/32c4cb047498da9301d64986ee0a646b.jpeg"),
                                                                      "Josh",
                                                                      "Lucas")
            ))
        } else {
            return Observable.just(FullChatItem.Group("fasf",
                                                      User.asStudent("1",
                                                                     IconImageUser("https://1833.fm/wp-content/uploads/2013/08/artworks-000055367262-c2ccus-t500x500.jpg"),
                                                                     "Josh",
                                                                     "Lucas"),
                                                      DateTime.now().minus(Duration.standardHours(3)),
                                                      IconImage("https://a3-images.myspacecdn.com/images03/30/1dfa855bd0b847458c1b73bb7a240972/300x300.jpg"),
                                                      "Test group",
                                                      listOf(User.asStudent("1",
                                                                            IconImageUser("https://1833.fm/wp-content/uploads/2013/08/artworks-000055367262-c2ccus-t500x500.jpg"),
                                                                            "Josh",
                                                                            "Lucas"),
                                                             User.asStudent("2",
                                                                            IconImageUser("https://a.d-cd.net/a67c026s-960.jpg"),
                                                                            "Farel",
                                                                            "Kayy")
                                                      )
            ))
        }
    }

    override fun loadChatPage(page: Int): Single<List<ChatMessage>> {
        if (3 < page) {
            return Single.just(emptyList())
        }

        return Single.just(listOf(
            ChatMessage.TextMessage(
                UUID.randomUUID().toString(),
                User.asStudent("1",
                               IconImageUser("http://cdn01.ru/files/users/images/32/c4/32c4cb047498da9301d64986ee0a646b.jpeg"),
                               "Josh",
                               "Lucas"),
                DateTime(2017, 12, 18, 18, 49),
                "first message weeee $page",
                emptyList()
            ),
            ChatMessage.TextMessage(
                UUID.randomUUID().toString(),
                User.asStudent("1",
                               IconImageUser("http://cdn01.ru/files/users/images/32/c4/32c4cb047498da9301d64986ee0a646b.jpeg"),
                               "Josh",
                               "Lucas"),
                DateTime(2017, 12, 18, 18, 40),
                "second message weeee $page",
                emptyList()
            ),
            ChatMessage.TextMessage(
                UUID.randomUUID().toString(),
                User.asStudent("1",
                               IconImageUser("http://cdn01.ru/files/users/images/32/c4/32c4cb047498da9301d64986ee0a646b.jpeg"),
                               "Josh",
                               "Lucas"),
                DateTime(2017, 12, 18, 17, 9),
                "yes, but i dont have any options… i have my work with me… try hard..\uD83D\uDE02 $page",
                emptyList()
            ),
            ChatMessage.TextMessage(
                UUID.randomUUID().toString(),
                User.asStudent("1",
                               IconImageUser("http://cdn01.ru/files/users/images/32/c4/32c4cb047498da9301d64986ee0a646b.jpeg"),
                               "Josh",
                               "Lucas"),
                DateTime(2017, 12, 17, 18, 49),
                "one more time message weeee $page",
                emptyList()
            ),
            ChatMessage.TextMessage(
                UUID.randomUUID().toString(),
                User.asStudent("1",
                               IconImageUser("http://cdn01.ru/files/users/images/32/c4/32c4cb047498da9301d64986ee0a646b.jpeg"),
                               "Josh",
                               "Lucas"),
                DateTime(2017, 12, 16, 8, 49),
                "are you here?",
                emptyList()
            ),
            ChatMessage.AttachmentMessage(
                UUID.randomUUID().toString(),
                User.asStudent("1",
                               IconImageUser("http://cdn01.ru/files/users/images/32/c4/32c4cb047498da9301d64986ee0a646b.jpeg"),
                               "Josh",
                               "Lucas"),
                DateTime(2017, 11, 1, 0, 9),
                "first attachment weeee $page",
                listOf(ChatAttachment.Image("http://www.gandex.ru/upl/oboi/gandex.ru-12641_7122_26_1408.jpg")),
                emptyList()
            ),
            ChatMessage.AttachmentMessage(
                UUID.randomUUID().toString(),
                User.asStudent("1",
                               IconImageUser("http://cdn01.ru/files/users/images/32/c4/32c4cb047498da9301d64986ee0a646b.jpeg"),
                               "Josh",
                               "Lucas"),
                DateTime(2017, 10, 14, 13, 3),
                "",
                listOf(ChatAttachment.Image("https://i1.i.ua/prikol/pic/7/9/454097.jpg"),
                       ChatAttachment.Image("http://klukva.org/uploads/posts/2013-02/1360925166_auto-001.jpg")),
                emptyList()
            ),
            ChatMessage.AttachmentMessage(
                UUID.randomUUID().toString(),
                User.asStudent("2",
                               IconImageUser("https://cs8.pikabu.ru/post_img/2017/01/05/5/1483598291183026970.jpg"),
                               "Sara",
                               "Ping"),
                DateTime(2017, 10, 14, 13, 2),
                "sip protocol",
                listOf(ChatAttachment.File("http://tasuka.idv.tw/SIP/SIP.pdf",
                                           "PDF",
                                           "https://cdn4.iconfinder.com/data/icons/file-extensions-1/64/pdfs-512.png",
                                           2938048L)),
                emptyList()
            )
        ))
            .delay(4, TimeUnit.SECONDS)
    }

    override fun loadTypingInfo(): Observable<ChatTypingInfo> {
        val user1 = User.asStudent("1",
                                   IconImageUser("http://cdn01.ru/files/users/images/32/c4/32c4cb047498da9301d64986ee0a646b.jpeg"),
                                   "Josh",
                                   "Lucas")
        val user2 = User.asStudent("2",
                                   IconImageUser("https://cs8.pikabu.ru/post_img/2017/01/05/5/1483598291183026970.jpg"),
                                   "Sara",
                                   "Ping")
        val random = SecureRandom()
        return Observable.interval(5, TimeUnit.SECONDS)
            .delay(random.nextInt(3000).toLong(), TimeUnit.MILLISECONDS)
            .map { ChatTypingInfo(if (random.nextBoolean()) user1 else user2, random.nextBoolean()) }
    }

    override fun loadSendingMessagesInfo(): Observable<List<ChatMessage>> = pendingMessages.toObservable()

    override fun loadNewMessages(): Observable<List<ChatMessage>> {
        return Observable.never()
    }

    override fun sendTextMessage(message: String) {
        synchronized(pendingMessages) {
            currentUser?.run {
                sendMessageInternal(
                    ChatMessage.TextMessage(UUID.randomUUID().toString(), this, DateTime.now(), message, emptyList(), ChatMyMessageState.PENDING),
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
                                                  this,
                                                  DateTime.now(),
                                                  "",
                                                  listOf(
                                                      ChatAttachment.LocalFile(fileResource,
                                                                               fileResource.typeName(),
                                                                               fileResource.typeIcon(context),
                                                                               fileResource.file.length())),
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
            .subscribe({
                           synchronized(pendingMessages) {
                               if (pendingMessages.contains(message)) {
                                   pendingMessages.replaceElement(message, it)
                                   if (it.myMessageState == ChatMyMessageState.SEND && message is ChatMessage.AttachmentMessage) {
                                       message.attachments.mapNotNull {
                                           when (it) {
                                               is ChatAttachment.LocalImage -> it.imageFile
                                               is ChatAttachment.LocalFile  -> it.fileFile
                                               else                         -> null
                                           }
                                       }.forEach { it.cleanUp() }
                                   }
                               }
                           }
                       }, {})
    }

    private fun executeMessageSending(message: ChatMessage): Single<ChatMessage> {
        return Completable.complete()
            .delay(5, TimeUnit.SECONDS)
            .andThen(Single.error<ChatMessage>(IllegalArgumentException("error!")).map { message.changeState(ChatMyMessageState.SEND) })
            .onErrorReturnItem(message.changeState(ChatMyMessageState.SEND_FAILED))
    }
}

private fun ChatMessage.changeState(chatMyMessageState: ChatMyMessageState): ChatMessage =
    when (this) {
        is ChatMessage.TextMessage       -> copy(myMessageState = chatMyMessageState)
        is ChatMessage.AttachmentMessage -> copy(myMessageState = chatMyMessageState)
    }

private fun FileResource.typeName(): String = file.extension

private fun FileResource.typeIcon(context: Context): String {
    val typeName = typeName()

    val drawableId = context.resources.getIdentifier("ic_extension_$typeName", "drawable", context.packageName)
    return (if (0 == drawableId) R.drawable.ic_extension_unknown else drawableId).toFrescoImagePath()
}