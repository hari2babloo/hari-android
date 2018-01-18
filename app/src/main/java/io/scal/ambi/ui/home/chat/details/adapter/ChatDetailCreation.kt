package io.scal.ambi.ui.home.chat.details.adapter

import com.ambi.work.R
import com.ambi.work.databinding.ItemChatDetailsMessageInfoBinding
import io.scal.ambi.ui.global.base.adapter.AdapterDelegateBase
import io.scal.ambi.ui.home.chat.details.data.UIChatInfo

class ChatDetailCreation : AdapterDelegateBase<ItemChatDetailsMessageInfoBinding, List<Any>>() {

    override val layoutId: Int = R.layout.item_chat_details_message_info

    override fun isForViewType(items: List<Any>, position: Int): Boolean =
        items[position] is UIChatInfo

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ItemChatDetailsMessageInfoBinding, payloads: MutableList<Any>) {
        binding.element = items[position] as UIChatInfo
    }
}