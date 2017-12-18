package io.scal.ambi.ui.home.chat.list.data

import io.scal.ambi.entity.chat.SmallChatItem
import io.scal.ambi.extensions.view.IconImage
import org.joda.time.DateTime

data class UIChatList constructor(val chatInfo: SmallChatItem,
                                  val uid: String,
                                  val icon: IconImage,
                                  val title: String,
                                  val lastMessage: String,
                                  val lastMessageDateTime: DateTime,
                                  val hasNewMessages: Boolean,
                                  val filterType: UIChatListFilter) {

    constructor(chatInfo: SmallChatItem,
                lastMessage: String,
                lastMessageDateTime: DateTime,
                hasNewMessages: Boolean,
                filterType: UIChatListFilter) :
        this(chatInfo, chatInfo.uid, chatInfo.icon, chatInfo.title, lastMessage, lastMessageDateTime, hasNewMessages, filterType)
}