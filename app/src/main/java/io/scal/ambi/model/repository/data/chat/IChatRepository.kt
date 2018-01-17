package io.scal.ambi.model.repository.data.chat

import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.model.repository.data.chat.data.ChatChannelChanged
import io.scal.ambi.model.repository.data.chat.data.ChatChannelInfo
import io.scal.ambi.model.repository.data.chat.data.ChatClientChanged

interface IChatRepository {

    fun observeChatClientChanged(): Observable<ChatClientChanged>

    fun observeChatChangedEvents(chatUids: List<String>): Observable<ChatChannelChanged>

    fun loadAllChannelList(page: Int): Single<List<ChatChannelInfo>>

    fun createChat(createInfo: ChatCreateInfo, currentUser: String): Single<ChatChannelInfo>

    class ChatCreateInfo(val organizationSmall: ChatChannelInfo.OrganizationSmall?,
                         val chatTitle: String?,
                         val memberUids: List<String>)
}