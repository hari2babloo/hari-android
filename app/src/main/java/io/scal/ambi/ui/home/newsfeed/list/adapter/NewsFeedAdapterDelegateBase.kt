package io.scal.ambi.ui.home.newsfeed.list.adapter

import android.databinding.ViewDataBinding
import android.support.annotation.CallSuper
import io.scal.ambi.BR
import io.scal.ambi.ui.global.base.adapter.AdapterDelegateBase
import io.scal.ambi.ui.home.newsfeed.list.NewsFeedViewModel

internal abstract class NewsFeedAdapterDelegateBase<in Binding : ViewDataBinding>(private val viewModel: NewsFeedViewModel) :
    AdapterDelegateBase<Binding, List<Any>>() {

    @CallSuper
    override fun onBindViewHolder(items: List<Any>, position: Int, binding: Binding, payloads: MutableList<Any>) {
        binding.setVariable(BR.viewModel, viewModel)
    }
}