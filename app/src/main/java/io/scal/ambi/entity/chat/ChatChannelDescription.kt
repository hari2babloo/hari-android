package io.scal.ambi.entity.chat

import io.scal.ambi.extensions.view.IconImage
import org.joda.time.DateTime
import java.io.Serializable

data class ChatChannelDescription(val uid: String,
                                  val title: String,
                                  val iconImage: IconImage,
                                  val creationDateTime: DateTime) : Serializable