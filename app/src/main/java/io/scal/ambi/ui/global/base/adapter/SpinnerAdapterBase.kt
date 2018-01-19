package io.scal.ambi.ui.global.base.adapter

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.ambi.work.BR

abstract class SpinnerAdapterBase<T>(private var data: List<T> = emptyList()) : BaseAdapter() {

    abstract val itemLayoutId: Int
    open val itemLayoutDropdownId: Int? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = createBinding<ViewDataBinding>(convertView, parent, itemLayoutId)

        onBindItem(position, binding)

        return binding.root
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return itemLayoutDropdownId?.let {
            val binding = createBinding<ViewDataBinding>(convertView, parent, it)

            onBindItem(position, binding)

            binding.root
        }
            ?: super.getDropDownView(position, convertView, parent)
    }

    protected open fun <ItemBinding : ViewDataBinding> createBinding(convertView: View?, parent: ViewGroup, layoutId: Int): ItemBinding =
        if (null == convertView) {
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutId, parent, false)
        } else {
            DataBindingUtil.bind(convertView)
        }

    protected open fun onBindItem(position: Int, binding: ViewDataBinding) {
        binding.setVariable(BR.item, getItem(position))
        binding.executePendingBindings()
    }

    override fun getItem(position: Int): T =
        data[position]

    override fun getItemId(position: Int): Long =
        position.toLong()

    override fun getCount(): Int =
        data.size

    fun updateData(newData: List<T>) {
        if (newData != data) {
            data = newData
            notifyDataSetChanged()
        }
    }
}