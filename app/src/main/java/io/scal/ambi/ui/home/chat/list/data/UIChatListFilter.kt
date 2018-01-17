package io.scal.ambi.ui.home.chat.list.data

import io.scal.ambi.R

sealed class UIChatListFilter(val titleId: Int,
                              val iconId: Int) {

    object AllChats : UIChatListFilter(R.string.chat_list_filter_all, R.drawable.ic_chat_filter_all)

    object OrganizationChats : UIChatListFilter(R.string.chat_list_filter_organization, R.drawable.ic_chat_filter_group)

/*
    object GroupChats : UIChatListFilter(R.string.chat_list_filter_group, R.drawable.ic_chat_filter_group)

    object ClassChats : UIChatListFilter(R.string.chat_list_filter_class, R.drawable.ic_chat_filter_class)
*/
}