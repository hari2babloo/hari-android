package io.scal.ambi.ui.home.more

import com.ambi.work.R
import com.ambi.work.databinding.MoreMenuItemBinding

internal class MoreAdapterDelegate(viewModel: IMoreItemViewModel) :
        MoreAdapterDelegateBase<MoreMenuItemBinding>(viewModel) {

    override val layoutId: Int = R.layout.more_menu_item

    override fun isForViewType(items: List<Any>, position: Int): Boolean {
        return items[position] is MoreItem
    }

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: MoreMenuItemBinding, payloads: MutableList<Any>) {
        binding.element = items[position] as? MoreItem
        super.onBindViewHolder(items, position, binding, payloads)
    }
}