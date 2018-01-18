package io.scal.ambi.model.repository.data.chat

import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.model.repository.data.chat.data.ChatChannelChanged
import io.scal.ambi.model.repository.data.chat.data.ChatChannelInfo
import io.scal.ambi.model.repository.data.chat.data.ChatChannelMessage
import io.scal.ambi.model.repository.data.chat.data.ChatClientChanged
import io.scal.ambi.ui.global.picker.FileResource

interface IChatRepository {

    fun listenForPushToken()

    fun observeChatClientChanged(): Observable<ChatClientChanged>

    fun observeChatChangedEvents(chatUids: List<String>): Observable<ChatChannelChanged>

    fun getChannelInfo(chatUid: String): Single<ChatChannelInfo>

    fun loadAllChannelList(page: Int): Single<List<ChatChannelInfo>>

    fun createChat(createInfo: ChatCreateInfo, currentUser: String): Single<ChatChannelInfo>

    fun loadChatMessages(chatUid: String, lastMessageIndex: Long?): Single<List<ChatChannelMessage>>

    class ChatCreateInfo(val organizationSmall: ChatChannelInfo.OrganizationSmall?,
                         val chatTitle: String?,
                         val memberUids: List<String>)

    fun sendChatMessage(chatUid: String, message: String, attachments: List<FileResource>?): Single<ChatChannelMessage>
}