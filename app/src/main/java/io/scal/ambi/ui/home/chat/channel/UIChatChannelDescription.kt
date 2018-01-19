package io.scal.ambi.ui.home.chat.channel

import io.scal.ambi.entity.chat.ChatChannelDescription
import io.scal.ambi.extensions.view.IconImage

data class UIChatChannelDescription(val icon: IconImage,
                                    val title: String,
                                    val selected: Boolean,
                                    val channelDescription: ChatChannelDescription)