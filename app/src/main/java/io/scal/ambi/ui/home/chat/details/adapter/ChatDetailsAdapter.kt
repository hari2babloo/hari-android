package io.scal.ambi.ui.home.chat.details.adapter

import io.scal.ambi.R
import io.scal.ambi.ui.global.base.adapter.AdapterDelegateStaticView
import io.scal.ambi.ui.global.base.adapter.HeaderFooterList
import io.scal.ambi.ui.global.base.adapter.RecyclerViewAdapterDelegated
import io.scal.ambi.ui.home.chat.details.ChatDetailsViewModel
import io.scal.ambi.ui.home.chat.details.data.UIChatMessage

class ChatDetailsAdapter(viewModel: ChatDetailsViewModel) : RecyclerViewAdapterDelegated<Any>() {

    private val footerElement = Any()

    override var dataList: List<Any> = HeaderFooterList(null, footerElement, emptyList())
    private var messageList: HeaderFooterList
        get() = dataList as HeaderFooterList
        set(value) {
            dataList = value
        }

    init {
        addDelegate(ChatDetailsTextMessage(viewModel))
        addDelegate(ChatDetailsImageMessage(viewModel))
        addDelegate(ChatDetailsAttachmentMessage(viewModel))
        addDelegate(ChatDetailsTyping())
        addDelegate(AdapterDelegateStaticView(footerElement, R.layout.item_adapter_progress_footer))

        messageList.updateFooterVisibility(false, this)
    }

    fun updateData(data: List<Any>) {
        messageList = messageList.copy(data = data)
        notifyDataSetChanged()
    }

    fun releaseData() {
        messageList = messageList.copy(data = emptyList())
        notifyDataSetChanged()
    }

    fun showPageProgress(show: Boolean) {
        messageList.updateFooterVisibility(show, this)
    }
}