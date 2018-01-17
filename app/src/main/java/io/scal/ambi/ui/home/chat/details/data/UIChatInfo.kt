package io.scal.ambi.ui.home.chat.details.data

import io.scal.ambi.entity.chat.ChatChannelDescription
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.view.IconImage

data class UIChatInfo(val channelDescription: ChatChannelDescription,
                      val friendlyChatDescriptions: List<ChatChannelDescription>?,
                      val icon: IconImage,
                      val title: CharSequence,
                      val description: CharSequence,
                      val members: List<User>,
                      val canAddUsers: Boolean?)