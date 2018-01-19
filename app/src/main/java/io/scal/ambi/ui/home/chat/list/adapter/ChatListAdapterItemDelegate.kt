package io.scal.ambi.ui.home.chat.list.adapter

import com.ambi.work.R
import com.ambi.work.databinding.ItemChatListItemBinding
import io.scal.ambi.ui.global.base.adapter.AdapterDelegateBase
import io.scal.ambi.ui.home.chat.list.ChatListViewModel
import io.scal.ambi.ui.home.chat.list.data.UIChatList

internal class ChatListAdapterItemDelegate(private val viewModel: ChatListViewModel) : AdapterDelegateBase<ItemChatListItemBinding, List<Any>>() {

    override val layoutId: Int = R.layout.item_chat_list_item

    override fun isForViewType(items: List<Any>, position: Int): Boolean =
        items[position] is UIChatList

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ItemChatListItemBinding, payloads: MutableList<Any>) {
        binding.element = items[position] as UIChatList
        binding.viewModel = viewModel
    }
}