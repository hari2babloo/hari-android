package io.scal.ambi.ui.home.chat.details.adapter

import com.ambi.work.R
import com.ambi.work.databinding.ItemChatDetailsMessageTextBinding
import io.scal.ambi.ui.global.base.adapter.AdapterDelegateBase
import io.scal.ambi.ui.home.chat.details.ChatDetailsViewModel
import io.scal.ambi.ui.home.chat.details.data.UIChatMessage

internal class ChatDetailsTextMessage(private val viewModel: ChatDetailsViewModel) : AdapterDelegateBase<ItemChatDetailsMessageTextBinding, List<Any>>() {

    override val layoutId: Int = R.layout.item_chat_details_message_text

    override fun isForViewType(items: List<Any>, position: Int): Boolean =
        items[position] is UIChatMessage.TextMessage

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ItemChatDetailsMessageTextBinding, payloads: MutableList<Any>) {
        binding.element = items[position] as UIChatMessage.TextMessage
        binding.viewModel = viewModel
    }
}