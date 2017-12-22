package io.scal.ambi.ui.home.chat.details.adapter

import android.support.v7.util.DiffUtil
import io.scal.ambi.R
import io.scal.ambi.extensions.binding.DefaultDiffCallback
import io.scal.ambi.ui.global.base.adapter.AdapterDelegateStaticView
import io.scal.ambi.ui.global.base.adapter.HeaderFooterList
import io.scal.ambi.ui.global.base.adapter.RecyclerViewAdapterDelegated
import io.scal.ambi.ui.home.chat.details.ChatDetailsViewModel
import io.scal.ambi.ui.home.chat.details.data.UIChatDate
import io.scal.ambi.ui.home.chat.details.data.UIChatInfo
import io.scal.ambi.ui.home.chat.details.data.UIChatMessage
import io.scal.ambi.ui.home.chat.details.data.UIChatTyping

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
        addDelegate(ChatDetailCreation())
        addDelegate(ChatDetailsTyping())
        addDelegate(AdapterDelegateStaticView(footerElement, R.layout.item_adapter_progress_footer))
        addDelegate(ChatDetailDate())

        setHasStableIds(true)

        messageList.updateFooterVisibility(false, this)
    }

    override fun getItemId(position: Int): Long {
        val item = messageList[position]
        return when (item) {
            is UIChatMessage -> item.uid.hashCode().let { if (it <= 0) it.toLong() + Integer.MAX_VALUE else it.toLong() }
            is UIChatInfo    -> -1L
            is UIChatTyping  -> -2L
            footerElement    -> 0L
            is UIChatDate    -> item.dateVal.hashCode().let { if (it >= -4) it.toLong() - Integer.MAX_VALUE else it.toLong() }
            else             -> throw IllegalArgumentException("wrong item")
        }
    }

    fun updateData(data: List<Any>) {
        if (!messageList.hasSameData(data)) {
            val oldMessageList = messageList
            messageList = messageList.copy(data = data)

            val diffResult = DiffUtil.calculateDiff(DefaultDiffCallback(oldMessageList, messageList), true)
            diffResult.dispatchUpdatesTo(this)
        }
    }

    fun releaseData() {
        messageList = messageList.copy(data = emptyList())
        notifyDataSetChanged()
    }

    fun showPageProgress(show: Boolean) {
        messageList.updateFooterVisibility(show, this)
    }
}