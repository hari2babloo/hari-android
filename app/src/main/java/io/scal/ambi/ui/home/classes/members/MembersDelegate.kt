package io.scal.ambi.ui.home.classes.members

import com.ambi.work.R
import com.ambi.work.databinding.ItemMembersBinding
import io.scal.ambi.ui.home.classes.about.MembersData

internal class MembersDelegate(viewModel: IMembersViewModel) :
    MembersAdapterDelegateBase<ItemMembersBinding>(viewModel) {

    override val layoutId: Int = R.layout.item_members

    override fun isForViewType(items: List<Any>, position: Int): Boolean {
        return items[position] is MembersData
    }

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ItemMembersBinding, payloads: MutableList<Any>) {
        binding.element = items[position] as? MembersData
        super.onBindViewHolder(items, position, binding, payloads)
    }
}