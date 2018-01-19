package io.scal.ambi.ui.home.chat.details.adapter

import com.ambi.work.R
import com.ambi.work.databinding.ItemChatDetailsMessageDateBinding
import io.scal.ambi.ui.global.base.adapter.AdapterDelegateBase
import io.scal.ambi.ui.home.chat.details.data.UIChatDate

class ChatDetailDate : AdapterDelegateBase<ItemChatDetailsMessageDateBinding, List<Any>>() {

    override val layoutId: Int = R.layout.item_chat_details_message_date

    override fun isForViewType(items: List<Any>, position: Int): Boolean =
        items[position] is UIChatDate

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ItemChatDetailsMessageDateBinding, payloads: MutableList<Any>) {
        binding.element = items[position] as UIChatDate
    }
}