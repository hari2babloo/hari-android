package io.scal.ambi.model.interactor.home.chat

import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.R
import io.scal.ambi.entity.User
import io.scal.ambi.entity.chat.ChatAttachment
import io.scal.ambi.entity.chat.ChatMessage
import io.scal.ambi.entity.chat.SmallChatItem
import io.scal.ambi.extensions.binding.binders.toFrescoImagePath
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.IconImageUser
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import org.joda.time.DateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ChatListInteractor @Inject constructor(private val localUserDataRepository: ILocalUserDataRepository) : IChatListInteractor {

    override fun loadCurrentUser(): Observable<User> =
        localUserDataRepository.observeCurrentUser()

    override fun loadChatListPage(page: Int): Single<List<SmallChatItem>> {
        return Single.just(listOf(
            SmallChatItem.Direct("$page _ 0",
                                 DateTime(2017, 12, 15, 16, 10),
                                 User("2", IconImageUser(R.drawable.ic_tab_calendar_icon.toFrescoImagePath()), "Brad", "Jiss"),
                                 null,
                                 false
            ),
            SmallChatItem.Direct("$page _ 1",
                                 DateTime(2017, 12, 15, 15, 10),
                                 User("2", IconImageUser(R.drawable.ic_action_image.toFrescoImagePath()), "Brad", "Jiss"),
                                 ChatMessage.TextMessage(User("2", IconImageUser(R.drawable.ic_action_image.toFrescoImagePath()), "Brad", "Jiss"),
                                                         DateTime(2017, 12, 15, 15, 30),
                                                         "Hey! How are you ? New",
                                                         emptyList()),
                                 true
            ),
            SmallChatItem.Direct("$page _ 2",
                                 DateTime(2017, 12, 11, 1, 23),
                                 User("2", IconImageUser(R.drawable.ic_tab_notification_icon.toFrescoImagePath()), "Brad", "Jiss"),
                                 ChatMessage.AttachmentMessage(User("2",
                                                                    IconImageUser(R.drawable.ic_action_image.toFrescoImagePath()),
                                                                    "Brad",
                                                                    "Jiss"),
                                                               DateTime(2017, 12, 11, 1, 24),
                                                               "Hey! How are you ? NO",
                                                               listOf(ChatAttachment.Image("https://i.ytimg.com/vi/nBlT6pEyq5k/maxresdefault.jpg")),
                                                               emptyList()),
                                 false
            ),
            SmallChatItem.Direct("$page _ 3",
                                 DateTime(2017, 11, 10, 6, 0),
                                 User("2", IconImageUser(R.drawable.ic_action_image.toFrescoImagePath()), "Brad", "Jiss"),
                                 ChatMessage.AttachmentMessage(User("2",
                                                                    IconImageUser(R.drawable.ic_action_image.toFrescoImagePath()),
                                                                    "Brad",
                                                                    "Jiss"),
                                                               DateTime(2017, 11, 10, 6, 1),
                                                               "Hey! How are you ? NO",
                                                               listOf(ChatAttachment.Image("https://i.ytimg.com/vi/nBlT6pEyq5k/maxresdefault.jpg"),
                                                                      ChatAttachment.Image("https://i.imgur.com/eGJJvdd.gif")
                                                               ),
                                                               emptyList()
                                 ),
                                 false
            ),
            SmallChatItem.Group("$page _ 4",
                                DateTime(2017, 10, 15, 23, 59),
                                IconImage(R.drawable.ic_tab_more_icon.toFrescoImagePath()),
                                "This is a test group YES",
                                listOf(
                                    User("2", IconImageUser(R.drawable.ic_action_image.toFrescoImagePath()), "Brad", "Jiss"),
                                    User("3", IconImageUser(R.drawable.ic_action_image.toFrescoImagePath()), "Frank", "Poooo"),
                                    User("4", IconImageUser(R.drawable.ic_action_image.toFrescoImagePath()), "Bruse", "Leeeee")
                                ),
                                ChatMessage.AttachmentMessage(User("2",
                                                                   IconImageUser(R.drawable.ic_action_image.toFrescoImagePath()),
                                                                   "Brad",
                                                                   "Jiss"),
                                                              DateTime(2017, 12, 15, 17, 31),
                                                              "Hey! How are you ?",
                                                              listOf(ChatAttachment.Image("https://i.ytimg.com/vi/nBlT6pEyq5k/maxresdefault.jpg"),
                                                                     ChatAttachment.Image("https://i.imgur.com/eGJJvdd.gif")
                                                              ),
                                                              emptyList()
                                ),
                                true
            )
        )).delay(4000, TimeUnit.MILLISECONDS)
    }
}