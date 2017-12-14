package io.scal.ambi.entity.chat

import io.scal.ambi.extensions.view.IconImage
import org.joda.time.DateTime

data class SmallChatItem(val uid: String,
                         val icon: IconImage,
                         val title: String,
                         val lastMessage: String,
                         val lastMessageDateTime: DateTime)