package io.scal.ambi.ui.global.base.adapter

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

abstract class RecyclerViewAdapterBase : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override open fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutId = onCreateBindingLayoutId(parent, viewType)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context), layoutId, parent, false)
        onCreateBinding(binding, viewType)
        return BindingViewHolder(binding)
    }

    protected open fun onCreateBinding(binding: ViewDataBinding, viewType: Int) {}

    protected abstract fun onCreateBindingLayoutId(parent: ViewGroup, viewType: Int): Int

    override open fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is BindingViewHolder<*>) {
            onBindBinding(holder.binding, holder, position)
            holder.binding.executePendingBindings()
        }
    }

    protected abstract fun onBindBinding(binding: ViewDataBinding, holder: BindingViewHolder<*>, position: Int)

    protected open class BindingViewHolder<out Binding : ViewDataBinding>(internal val binding: Binding) : RecyclerView.ViewHolder(binding.root)
}