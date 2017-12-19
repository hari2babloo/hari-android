package io.scal.ambi.ui.home.chat.details.adapter

import android.support.v7.util.DiffUtil
import io.scal.ambi.R
import io.scal.ambi.extensions.binding.DefaultDiffCallback
import io.scal.ambi.ui.global.base.adapter.AdapterDelegateStaticView
import io.scal.ambi.ui.global.base.adapter.HeaderFooterList
import io.scal.ambi.ui.global.base.adapter.RecyclerViewAdapterDelegated
import io.scal.ambi.ui.home.chat.details.ChatDetailsViewModel

class ChatDetailsAdapter(viewModel: ChatDetailsViewModel) : RecyclerViewAdapterDelegated<Any>() {

    private val footerElement = Any()

    override var dataList: List<Any> = HeaderFooterList(null, footerElement, emptyList())
    private var messageList: HeaderFooterList
        get() = dataList as HeaderFooterList
        set(value) {
            dataList = value
        }

    init {
        addDelegate(ChatDetailCreation())
        addDelegate(ChatDetailsTextMessage(viewModel))
        addDelegate(ChatDetailsImageMessage(viewModel))
        addDelegate(ChatDetailsAttachmentMessage(viewModel))
        addDelegate(ChatDetailDate())
        addDelegate(ChatDetailsTyping())
        addDelegate(AdapterDelegateStaticView(footerElement, R.layout.item_adapter_progress_footer))

        messageList.updateFooterVisibility(false, this)
    }

    fun updateData(data: List<Any>) {
        val oldMessageList = messageList
        messageList = messageList.copy(data = data)

        val diffResult = DiffUtil.calculateDiff(DefaultDiffCallback(oldMessageList, messageList), true)
        diffResult.dispatchUpdatesTo(this)
    }

    fun releaseData() {
        messageList = messageList.copy(data = emptyList())
        notifyDataSetChanged()
    }

    fun showPageProgress(show: Boolean) {
        messageList.updateFooterVisibility(show, this)
    }
}