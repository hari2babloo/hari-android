package io.scal.ambi.entity.chat

import io.scal.ambi.entity.User

data class ChatTypingInfo(val user: User,
                          val typing: Boolean)