package io.scal.ambi.ui.home.chat.channel

import android.databinding.ViewDataBinding
import android.view.ViewGroup
import com.ambi.work.BR
import com.ambi.work.R
import io.scal.ambi.ui.global.base.adapter.RecyclerViewAdapterBase

internal class ChatChannelSelectionAdapter(private val viewModel: ChatChannelSelectionViewModel,
                                           private val data: List<UIChatChannelDescription>) : RecyclerViewAdapterBase() {

    override fun onCreateBindingLayoutId(parent: ViewGroup, viewType: Int): Int =
        R.layout.item_chat_channel_selection

    override fun onBindBinding(binding: ViewDataBinding, holder: BindingViewHolder<*>, position: Int) {
        binding.setVariable(BR.element, data[position])
        binding.setVariable(BR.viewModel, viewModel)
    }

    override fun getItemCount(): Int =
        data.size
}