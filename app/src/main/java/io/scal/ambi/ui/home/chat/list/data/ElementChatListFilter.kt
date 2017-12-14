package io.scal.ambi.ui.home.chat.list.data

import io.scal.ambi.R

sealed class ElementChatListFilter(open val titleId: Int,
                                   open val iconId: Int) {

    object AllChats : ElementChatListFilter(R.string.chat_list_filter_all,
                                                                                R.drawable.ic_chat_filter_all)

    object GroupChats : ElementChatListFilter(R.string.chat_list_filter_group,
                                                                                  R.drawable.ic_chat_filter_group)

    object ClassChats : ElementChatListFilter(R.string.chat_list_filter_class,
                                                                                  R.drawable.ic_chat_filter_class)
}