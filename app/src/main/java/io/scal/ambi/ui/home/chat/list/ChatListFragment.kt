package io.scal.ambi.ui.home.chat.list

import io.scal.ambi.R
import io.scal.ambi.databinding.FragmentChatListBinding
import io.scal.ambi.ui.global.base.fragment.BaseNavigationFragment
import kotlin.reflect.KClass

class ChatListFragment : BaseNavigationFragment<ChatListViewModel, FragmentChatListBinding>() {

    override val layoutId: Int = R.layout.fragment_chat_list
    override val viewModelClass: KClass<ChatListViewModel> = ChatListViewModel::class
}