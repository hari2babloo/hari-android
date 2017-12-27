package io.scal.ambi.model.interactor.home.chat

import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.R
import io.scal.ambi.entity.chat.ChatAttachment
import io.scal.ambi.entity.chat.ChatChannelDescription
import io.scal.ambi.entity.chat.ChatMessage
import io.scal.ambi.entity.chat.PreviewChatItem
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.binding.binders.toFrescoImagePath
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.IconImageUser
import io.scal.ambi.model.repository.data.chat.IChatRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import org.joda.time.DateTime
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ChatListInteractor @Inject constructor(private val localUserDataRepository: ILocalUserDataRepository,
                                             private val chatRepository: IChatRepository) : IChatListInteractor {

    override fun loadCurrentUser(): Observable<User> =
        localUserDataRepository.observeCurrentUser()

    override fun loadChatListPage(page: Int): Single<List<PreviewChatItem>> {
        val groupDescription = ChatChannelDescription("$page _ 4",
                                                      "This is a test group YES",
                                                      IconImage("https://a3-images.myspacecdn.com/images03/30/1dfa855bd0b847458c1b73bb7a240972/300x300.jpg"),
                                                      DateTime(2017, 10, 15, 23, 59))
        return Single.just(listOf(
            PreviewChatItem.Direct(ChatChannelDescription("$page _ 0",
                                                          "Brad Jiss",
                                                          IconImageUser(R.drawable.ic_tab_calendar_icon.toFrescoImagePath()),
                                                          DateTime(2017, 12, 15, 16, 10)),
                                   User.asStudent("2",
                                                  IconImageUser(R.drawable.ic_tab_calendar_icon.toFrescoImagePath()),
                                                  "Brad",
                                                  "Jiss"),
                                   null,
                                   false
            ),
            PreviewChatItem.Direct(ChatChannelDescription("$page _ 1",
                                                          "Brad Jiss 2",
                                                          IconImageUser(R.drawable.ic_action_image.toFrescoImagePath()),
                                                          DateTime(2017, 12, 15, 15, 10)),
                                   User.asStudent("2", IconImageUser(R.drawable.ic_action_image.toFrescoImagePath()), "Brad", "Jiss"),
                                   ChatMessage.TextMessage(UUID.randomUUID().toString(),
                                                           User.asStudent("2",
                                                                          IconImageUser(R.drawable.ic_action_image.toFrescoImagePath()),
                                                                          "Brad",
                                                                          "Jiss"),
                                                           DateTime(2017, 12, 15, 15, 30),
                                                           "Hey! How are you ? New",
                                                           emptyList()),
                                   true
            ),
            PreviewChatItem.Direct(ChatChannelDescription("$page _ 2",
                                                          "Brad Jiss 3",
                                                          IconImageUser(R.drawable.ic_tab_notification_icon.toFrescoImagePath()),
                                                          DateTime(2017, 12, 11, 1, 23)),
                                   User.asStudent("2",
                                                  IconImageUser(R.drawable.ic_tab_notification_icon.toFrescoImagePath()),
                                                  "Brad",
                                                  "Jiss"),
                                   ChatMessage.AttachmentMessage(UUID.randomUUID().toString(),
                                                                 User.asStudent("2",
                                                                                IconImageUser(R.drawable.ic_action_image.toFrescoImagePath()),
                                                                                "Brad",
                                                                                "Jiss"),
                                                                 DateTime(2017, 12, 11, 1, 24),
                                                                 "Hey! How are you ? NO",
                                                                 listOf(ChatAttachment.Image("https://i.ytimg.com/vi/nBlT6pEyq5k/maxresdefault.jpg")),
                                                                 emptyList()),
                                   false
            ),
            PreviewChatItem.Direct(ChatChannelDescription("$page _ 3",
                                                          "Brad Jiss 4",
                                                          IconImageUser(R.drawable.ic_action_image.toFrescoImagePath()),
                                                          DateTime(2017, 11, 10, 6, 0)),
                                   User.asStudent("2", IconImageUser(R.drawable.ic_action_image.toFrescoImagePath()), "Brad", "Jiss"),
                                   ChatMessage.AttachmentMessage(UUID.randomUUID().toString(),
                                                                 User.asStudent("2",
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
            PreviewChatItem.Group(groupDescription,
                                  listOf(groupDescription),
                                  IconImage(R.drawable.ic_tab_more_icon.toFrescoImagePath()),
                                  listOf(
                                      User.asStudent("2", IconImageUser(R.drawable.ic_action_image.toFrescoImagePath()), "Brad", "Jiss"),
                                      User.asStudent("3",
                                                     IconImageUser(R.drawable.ic_action_image.toFrescoImagePath()),
                                                     "Frank",
                                                     "Poooo"),
                                      User.asStudent("4",
                                                     IconImageUser(R.drawable.ic_action_image.toFrescoImagePath()),
                                                     "Bruse",
                                                     "Leeeee")
                                  ),
                                  ChatMessage.AttachmentMessage(UUID.randomUUID().toString(),
                                                                User.asStudent("2",
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