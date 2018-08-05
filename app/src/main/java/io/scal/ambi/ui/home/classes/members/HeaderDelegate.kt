package io.scal.ambi.ui.home.classes.members

import com.ambi.work.R
import com.ambi.work.databinding.ItemMembersHeaderBinding
import io.scal.ambi.ui.home.classes.about.Header

internal class HeaderDelegate(viewModel: IMembersViewModel) :
    MembersAdapterDelegateBase<ItemMembersHeaderBinding>(viewModel) {

    override val layoutId: Int = R.layout.item_members_header

    override fun isForViewType(items: List<Any>, position: Int): Boolean {
        return items[position] is Header
    }

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ItemMembersHeaderBinding, payloads: MutableList<Any>) {
        binding.element = items[position] as? Header
        super.onBindViewHolder(items, position, binding, payloads)
    }
}