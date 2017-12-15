package io.scal.ambi.ui.global.base.adapter

import android.databinding.ViewDataBinding

internal class AdapterDelegateStaticView<T>(private val element: T, elementLayoutId: Int) : AdapterDelegateBase<ViewDataBinding, List<T>>() {

    override val layoutId: Int = elementLayoutId

    override fun isForViewType(items: List<T>, position: Int): Boolean =
        element == items[position]

    override fun onBindViewHolder(items: List<T>, position: Int, binding: ViewDataBinding, payloads: MutableList<Any>) {}
}