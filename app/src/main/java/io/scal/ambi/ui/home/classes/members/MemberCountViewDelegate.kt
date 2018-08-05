package io.scal.ambi.ui.home.classes.members

import com.ambi.work.R
import com.ambi.work.databinding.ItemMembersCountViewBinding
import io.scal.ambi.ui.home.classes.about.MemberCount

internal class MemberCountViewDelegate(viewModel: IMembersViewModel) :
    MembersAdapterDelegateBase<ItemMembersCountViewBinding>(viewModel) {

    override val layoutId: Int = R.layout.item_members_count_view

    override fun isForViewType(items: List<Any>, position: Int): Boolean {
        return items[position] is MemberCount
    }

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ItemMembersCountViewBinding, payloads: MutableList<Any>) {
        binding.element = items[position] as? MemberCount
        super.onBindViewHolder(items, position, binding, payloads)
    }
}