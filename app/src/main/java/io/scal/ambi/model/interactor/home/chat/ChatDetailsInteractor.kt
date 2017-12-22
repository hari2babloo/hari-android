package io.scal.ambi.model.interactor.home.chat

import android.databinding.ObservableArrayList
import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.chat.*
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.IconImageUser
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import org.joda.time.DateTime
import org.joda.time.Duration
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

class ChatDetailsInteractor @Inject constructor(@Named("chatUid") private val chatUid: String,
                                                private val localUserDataRepository: ILocalUserDataRepository) : IChatDetailsInteractor {

    private var currentUser: User? = null
    private val pendingMessages = ObservableArrayList<ChatMessage>()

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
            .firstOrError()
            .toObservable()
    }

    override fun loadSendingMessagesInfo(): Observable<List<ChatMessage>> = pendingMessages.toObservable()

    override fun loadNewMessages(): Observable<List<ChatMessage>> {
        return Observable.never()
    }

    override fun sendTextMessage(message: String) {
        synchronized(pendingMessages) {
            currentUser?.run {
                sendMessageInternal(
                    ChatMessage.TextMessage(UUID.randomUUID().toString(), this, DateTime.now(), message, emptyList(), ChatMyMessageState.PENDING))
            }
        }
    }

    override fun resendMessage(uid: String) {
        synchronized(pendingMessages) {
            pendingMessages
                .firstOrNull { it.uid == uid }
                ?.run {
                    pendingMessages.remove(this)
                    val messageToResend =
                        when (this) {
                            is ChatMessage.TextMessage       -> copy(myMessageState = ChatMyMessageState.PENDING)
                            is ChatMessage.AttachmentMessage -> copy(myMessageState = ChatMyMessageState.PENDING)
                        }
                    sendMessageInternal(messageToResend)
                }
        }
    }

    private fun sendMessageInternal(message: ChatMessage) {
        pendingMessages.add(message)
    }
}