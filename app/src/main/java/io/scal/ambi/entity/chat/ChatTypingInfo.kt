package io.scal.ambi.entity.chat

import io.scal.ambi.entity.user.User

data class ChatTypingInfo(val user: User,
                          val typing: Boolean)