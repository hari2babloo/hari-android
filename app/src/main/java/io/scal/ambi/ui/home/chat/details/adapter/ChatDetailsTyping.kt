package io.scal.ambi.ui.home.chat.details.adapter

import com.ambi.work.R
import com.ambi.work.databinding.ItemChatDetailsMessageTypingBinding
import io.scal.ambi.ui.global.base.adapter.AdapterDelegateBase
import io.scal.ambi.ui.home.chat.details.data.UIChatTyping

class ChatDetailsTyping : AdapterDelegateBase<ItemChatDetailsMessageTypingBinding, List<Any>>() {

    override val layoutId: Int = R.layout.item_chat_details_message_typing

    override fun isForViewType(items: List<Any>, position: Int): Boolean =
        items[position] is UIChatTyping

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ItemChatDetailsMessageTypingBinding, payloads: MutableList<Any>) {
        binding.element = items[position] as UIChatTyping
    }
}