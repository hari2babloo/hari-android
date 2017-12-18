package io.scal.ambi.ui.home.chat.list.adapter

import io.scal.ambi.R
import io.scal.ambi.databinding.ItemChatListItemBinding
import io.scal.ambi.ui.global.base.adapter.AdapterDelegateBase
import io.scal.ambi.ui.home.chat.list.ChatListViewModel
import io.scal.ambi.ui.home.chat.list.data.ElementChatList

internal class ChatListAdapterItemDelegate(private val viewModel: ChatListViewModel) : AdapterDelegateBase<ItemChatListItemBinding, List<Any>>() {

    override val layoutId: Int = R.layout.item_chat_list_item

    override fun isForViewType(items: List<Any>, position: Int): Boolean =
        items[position] is ElementChatList

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ItemChatListItemBinding, payloads: MutableList<Any>) {
        binding.element = items[position] as ElementChatList
        binding.viewModel = viewModel
        binding.executePendingBindings()
    }
}