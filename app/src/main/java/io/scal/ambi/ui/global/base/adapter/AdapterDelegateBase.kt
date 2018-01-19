package io.scal.ambi.ui.global.base.adapter

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate

abstract class AdapterDelegateBase<in Binding : ViewDataBinding, T> : AdapterDelegate<T>() {

    abstract protected val layoutId: Int

    override final fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<Binding>(LayoutInflater.from(parent.context), layoutId, parent, false)
        initBinding(binding)
        return BindingViewHolder(binding)
    }

    @Suppress("UNCHECKED_CAST")
    override final fun onBindViewHolder(items: T, position: Int, holder: RecyclerView.ViewHolder, payloads: MutableList<Any>) {
        val binding = (holder as BindingViewHolder<*>).binding as Binding
        onBindViewHolder(items, position, binding, payloads)
        binding.executePendingBindings()
    }

    protected open fun initBinding(binding: Binding) {}

    protected abstract fun onBindViewHolder(items: T, position: Int, binding: Binding, payloads: MutableList<Any>)

    open class BindingViewHolder<out Binding : ViewDataBinding>(internal val binding: Binding) : RecyclerView.ViewHolder(binding.root)
}