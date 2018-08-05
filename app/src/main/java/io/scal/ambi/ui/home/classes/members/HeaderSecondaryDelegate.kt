package io.scal.ambi.ui.home.classes.members

import com.ambi.work.R
import com.ambi.work.databinding.ItemMembersHeaderSecondaryBinding
import io.scal.ambi.ui.home.classes.about.Header
import io.scal.ambi.ui.home.classes.about.HeaderSecondary

internal class HeaderSecondaryDelegate(viewModel: IMembersViewModel) :
    MembersAdapterDelegateBase<ItemMembersHeaderSecondaryBinding>(viewModel) {

    override val layoutId: Int = R.layout.item_members_header_secondary

    override fun isForViewType(items: List<Any>, position: Int): Boolean {
        return items[position] is HeaderSecondary
    }

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ItemMembersHeaderSecondaryBinding, payloads: MutableList<Any>) {
        binding.element = items[position] as? HeaderSecondary
        super.onBindViewHolder(items, position, binding, payloads)
    }
}