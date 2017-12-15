package io.scal.ambi.ui.home.newsfeed.list.adapter

import android.databinding.ViewDataBinding
import io.scal.ambi.BR
import io.scal.ambi.ui.global.base.adapter.AdapterDelegateBase
import io.scal.ambi.ui.home.newsfeed.list.NewsFeedViewModel

internal abstract class NewsFeedAdapterDelegateBase<in Binding : ViewDataBinding>(private val viewModel: NewsFeedViewModel) :
    AdapterDelegateBase<Binding, List<Any>>() {

    override fun initBinding(binding: Binding) {
        super.initBinding(binding)
        binding.setVariable(BR.viewModel, viewModel)
    }
}