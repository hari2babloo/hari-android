package io.scal.ambi.ui.home.chat.list.data

import io.scal.ambi.extensions.view.IconImage
import org.joda.time.DateTime

data class ElementChatList(val uid: String,
                           val icon: IconImage,
                           val title: String,
                           val lastMessage: String,
                           val lastMessageDateTime: DateTime,
                           val filterType: ElementChatListFilter)