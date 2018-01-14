package io.scal.ambi.model.repository.data.chat

import org.joda.time.DateTime

data class ChatChannelMessage(val uid: String,
                              val sender: String,
                              val sendDate: DateTime,
                              val message: String,
                              val media: Media?) {

    data class Media(val url: String, val image: Boolean, val fileName: String, val size: Long)
}