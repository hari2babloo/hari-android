package io.scal.ambi.model.interactor.home.chat

import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.User
import io.scal.ambi.entity.chat.ChatAttachment
import io.scal.ambi.entity.chat.ChatAttachmentType
import io.scal.ambi.entity.chat.ChatMessage
import io.scal.ambi.entity.chat.FullChatItem
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.IconImageUser
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import org.joda.time.DateTime
import org.joda.time.Duration
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Named

class ChatDetailsInteractor @Inject constructor(@Named("chatUid") private val chatUid: String,
                                                private val localUserDataRepository: ILocalUserDataRepository) : IChatDetailsInteractor {

    override fun loadCurrentUser(): Observable<User> =
        localUserDataRepository.observeCurrentUser()

    override fun loadChatInfo(): Observable<FullChatItem> {
        if (SecureRandom().nextBoolean()) {
            return Observable.just(FullChatItem.Direct("fasf",
                                                       DateTime.now().minus(Duration.standardHours(3)),
                                                       User("1",
                                                            IconImageUser("http://cdn01.ru/files/users/images/32/c4/32c4cb047498da9301d64986ee0a646b.jpeg"),
                                                            "Josh",
                                                            "Lucas")
            ))
        } else {
            return Observable.just(FullChatItem.Group("fasf",
                                                      User("1",
                                                           IconImageUser("https://1833.fm/wp-content/uploads/2013/08/artworks-000055367262-c2ccus-t500x500.jpg"),
                                                           "Josh",
                                                           "Lucas"),
                                                      DateTime.now().minus(Duration.standardHours(3)),
                                                      IconImage("https://a3-images.myspacecdn.com/images03/30/1dfa855bd0b847458c1b73bb7a240972/300x300.jpg"),
                                                      "Test group",
                                                      listOf(User("1",
                                                                  IconImageUser("https://1833.fm/wp-content/uploads/2013/08/artworks-000055367262-c2ccus-t500x500.jpg"),
                                                                  "Josh",
                                                                  "Lucas"),
                                                             User("2",
                                                                  IconImageUser("https://a.d-cd.net/a67c026s-960.jpg"),
                                                                  "Farel",
                                                                  "Kayy")
                                                      )
            ))
        }
    }

    override fun loadChatPage(page: Int): Single<List<ChatMessage>> {
        return Single.just(listOf(
            ChatMessage.TextMessage(
                User("1",
                     IconImageUser("http://cdn01.ru/files/users/images/32/c4/32c4cb047498da9301d64986ee0a646b.jpeg"),
                     "Josh",
                     "Lucas"),
                DateTime(2017, 12, 18, 18, 49),
                "first message weeee $page",
                emptyList()
            ),
            ChatMessage.TextMessage(
                User("1",
                     IconImageUser("http://cdn01.ru/files/users/images/32/c4/32c4cb047498da9301d64986ee0a646b.jpeg"),
                     "Josh",
                     "Lucas"),
                DateTime(2017, 12, 18, 18, 49),
                "second message weeee $page",
                emptyList()
            ),
            ChatMessage.TextMessage(
                User("1",
                     IconImageUser("http://cdn01.ru/files/users/images/32/c4/32c4cb047498da9301d64986ee0a646b.jpeg"),
                     "Josh",
                     "Lucas"),
                DateTime(2017, 12, 18, 18, 49),
                "third message weeee $page",
                emptyList()
            ),
            ChatMessage.TextMessage(
                User("1",
                     IconImageUser("http://cdn01.ru/files/users/images/32/c4/32c4cb047498da9301d64986ee0a646b.jpeg"),
                     "Josh",
                     "Lucas"),
                DateTime(2017, 12, 18, 18, 49),
                "one more time message weeee $page",
                emptyList()
            ),
            ChatMessage.TextMessage(
                User("1",
                     IconImageUser("http://cdn01.ru/files/users/images/32/c4/32c4cb047498da9301d64986ee0a646b.jpeg"),
                     "Josh",
                     "Lucas"),
                DateTime(2017, 12, 18, 18, 49),
                "are you here?",
                emptyList()
            ),
            ChatMessage.AttachmentMessage(
                User("1",
                     IconImageUser("http://cdn01.ru/files/users/images/32/c4/32c4cb047498da9301d64986ee0a646b.jpeg"),
                     "Josh",
                     "Lucas"),
                DateTime(2017, 12, 18, 18, 49),
                "first attachment weeee $page",
                listOf(ChatAttachment("http://www.gandex.ru/upl/oboi/gandex.ru-12641_7122_26_1408.jpg", ChatAttachmentType.IMAGE)),
                emptyList()
            ),
            ChatMessage.AttachmentMessage(
                User("1",
                     IconImageUser("http://cdn01.ru/files/users/images/32/c4/32c4cb047498da9301d64986ee0a646b.jpeg"),
                     "Josh",
                     "Lucas"),
                DateTime(2017, 12, 18, 18, 49),
                "",
                listOf(ChatAttachment("https://i1.i.ua/prikol/pic/7/9/454097.jpg", ChatAttachmentType.IMAGE),
                       ChatAttachment("http://klukva.org/uploads/posts/2013-02/1360925166_auto-001.jpg", ChatAttachmentType.IMAGE)),
                emptyList()
            ),
            ChatMessage.AttachmentMessage(
                User("1",
                     IconImageUser("http://cdn01.ru/files/users/images/32/c4/32c4cb047498da9301d64986ee0a646b.jpeg"),
                     "Josh",
                     "Lucas"),
                DateTime(2017, 12, 18, 18, 49),
                "sip protocol",
                listOf(ChatAttachment("http://tasuka.idv.tw/SIP/SIP.pdf", ChatAttachmentType.FILE)),
                emptyList()
            )
        ))
    }
}