package io.scal.ambi.ui.global.base.adapter

import android.databinding.ViewDataBinding
import android.graphics.Color

internal class AdapterDelegateStaticView<T>(private val element: T,
                                            elementLayoutId: Int,
                                            private val bgColor: Int = Color.TRANSPARENT) : AdapterDelegateBase<ViewDataBinding, List<T>>() {

    override val layoutId: Int = elementLayoutId

    override fun isForViewType(items: List<T>, position: Int): Boolean =
        element == items[position]

    override fun initBinding(binding: ViewDataBinding) {
        super.initBinding(binding)

        binding.root.setBackgroundColor(bgColor)
    }

    override fun onBindViewHolder(items: List<T>, position: Int, binding: ViewDataBinding, payloads: MutableList<Any>) {}
}