package io.scal.ambi.ui.home.chat.details.adapter

import com.ambi.work.R
import com.ambi.work.databinding.ItemChatDetailsMessageImageBinding
import io.scal.ambi.ui.global.base.adapter.AdapterDelegateBase
import io.scal.ambi.ui.home.chat.details.ChatDetailsViewModel
import io.scal.ambi.ui.home.chat.details.data.UIChatMessage

internal class ChatDetailsImageMessage(private val viewModel: ChatDetailsViewModel) : AdapterDelegateBase<ItemChatDetailsMessageImageBinding, List<Any>>() {

    override val layoutId: Int = R.layout.item_chat_details_message_image

    override fun isForViewType(items: List<Any>, position: Int): Boolean =
        items[position] is UIChatMessage.ImageMessage

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ItemChatDetailsMessageImageBinding, payloads: MutableList<Any>) {
        binding.element = items[position] as UIChatMessage.ImageMessage
        binding.viewModel = viewModel
    }
}