package io.scal.ambi.ui.home.chat.list.data

import io.scal.ambi.entity.chat.PreviewChatItem
import io.scal.ambi.extensions.view.IconImage
import org.joda.time.DateTime

data class UIChatList constructor(val chatInfo: PreviewChatItem,
                                  val uid: String,
                                  val icon: IconImage,
                                  val title: String,
                                  val lastMessage: String,
                                  val lastMessageDateTime: DateTime,
                                  val hasNewMessages: Boolean,
                                  val filterType: UIChatListFilter) {

    constructor(chatInfo: PreviewChatItem,
                lastMessage: String,
                lastMessageDateTime: DateTime,
                hasNewMessages: Boolean,
                filterType: UIChatListFilter) :
        this(chatInfo,
             chatInfo.description.uid,
             chatInfo.icon,
             chatInfo.description.title,
             lastMessage,
             lastMessageDateTime,
             hasNewMessages,
             filterType)

    override fun equals(other: Any?): Boolean {
        if (other !is UIChatList) return false
        return other.uid == uid
    }

    override fun hashCode(): Int {
        return uid.hashCode()
    }
}