package io.scal.ambi.ui.home.classes

import com.ambi.work.R
import com.ambi.work.databinding.ItemClassesBinding

internal class ClassesDelegate(viewModel: IClassesViewModel) :
    ClassesAdapterDelegateBase<ItemClassesBinding>(viewModel) {

    override val layoutId: Int = R.layout.item_classes

    override fun isForViewType(items: List<Any>, position: Int): Boolean {
        return items[position] is ClassesData
    }

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ItemClassesBinding, payloads: MutableList<Any>) {
        binding.element = items[position] as? ClassesData
        super.onBindViewHolder(items, position, binding, payloads)
    }
}