package io.scal.ambi.ui.home.chat.details.adapter

import com.ambi.work.R
import com.ambi.work.databinding.ItemChatDetailsMessageAttachmentBinding
import io.scal.ambi.ui.global.base.adapter.AdapterDelegateBase
import io.scal.ambi.ui.home.chat.details.ChatDetailsViewModel
import io.scal.ambi.ui.home.chat.details.data.UIChatMessage

internal class ChatDetailsAttachmentMessage(private val viewModel: ChatDetailsViewModel) : AdapterDelegateBase<ItemChatDetailsMessageAttachmentBinding, List<Any>>() {

    override val layoutId: Int = R.layout.item_chat_details_message_attachment

    override fun isForViewType(items: List<Any>, position: Int): Boolean =
        items[position] is UIChatMessage.AttachmentMessage

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ItemChatDetailsMessageAttachmentBinding, payloads: MutableList<Any>) {
        binding.element = items[position] as UIChatMessage.AttachmentMessage
        binding.viewModel = viewModel
    }
}