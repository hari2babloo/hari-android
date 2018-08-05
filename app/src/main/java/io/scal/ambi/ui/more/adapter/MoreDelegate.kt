package io.scal.ambi.ui.home.classes

import com.ambi.work.R
import com.ambi.work.databinding.ItemMoreBinding
import io.scal.ambi.ui.more.adapter.IMoreItemViewModel
import io.scal.ambi.ui.more.adapter.MoreAdapterDelegateBase
import io.scal.ambi.ui.more.data.MoreData

internal class MoreDelegate(viewModel: IMoreItemViewModel) :
    MoreAdapterDelegateBase<ItemMoreBinding>(viewModel) {

    override val layoutId: Int = R.layout.item_more

    override fun isForViewType(items: List<Any>, position: Int): Boolean {
        return items[position] is MoreData
    }

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ItemMoreBinding, payloads: MutableList<Any>) {
        binding.element = items[position] as? MoreData
        super.onBindViewHolder(items, position, binding, payloads)
    }
}