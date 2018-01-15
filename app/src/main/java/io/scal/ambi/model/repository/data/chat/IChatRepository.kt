package io.scal.ambi.model.repository.data.chat

import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.extensions.trueOrThrow
import io.scal.ambi.model.repository.data.chat.data.ChatChannelChanged
import io.scal.ambi.model.repository.data.chat.data.ChatChannelInfo
import io.scal.ambi.model.repository.data.chat.data.ChatClientChanged

interface IChatRepository {

    fun observeChatClientChanged(): Observable<ChatClientChanged>

    fun observeChatChangedEvents(chatUids: List<String>): Observable<ChatChannelChanged>

    fun loadAllChannelList(page: Int): Single<List<ChatChannelInfo>>

    fun createChat(createInfo: ChatCreateInfo, currentUser: String): Single<ChatChannelInfo>

    sealed class ChatCreateInfo(val type: ChatChannelInfo.Type,
                                val name: String?,
                                val memberUids: List<String>) {

        class Organization(type: ChatChannelInfo.Type,
                           val orgName: String,
                           val orgSlug: String,
                           val origin: String,
                           name: String?,
                           memberUids: List<String>) : ChatCreateInfo(type, name, memberUids) {
            init {
                (type == ChatChannelInfo.Type.ORG_CLASS || type == ChatChannelInfo.Type.ORG_CLASS).trueOrThrow("wrong type for org type")
            }
        }

        class Simple(name: String?, memberUids: List<String>) : ChatCreateInfo(ChatChannelInfo.Type.SIMPLE, name, memberUids)
    }
}