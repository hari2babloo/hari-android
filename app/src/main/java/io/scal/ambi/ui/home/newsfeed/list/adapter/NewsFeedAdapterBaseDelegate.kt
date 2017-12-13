package io.scal.ambi.ui.home.newsfeed.list.adapter

import android.databinding.ViewDataBinding
import io.scal.ambi.BR
import io.scal.ambi.ui.global.base.adapter.BaseAdapterDelegate
import io.scal.ambi.ui.home.newsfeed.list.NewsFeedViewModel

abstract class NewsFeedAdapterBaseDelegate<in Binding : ViewDataBinding>(private val viewModel: NewsFeedViewModel) :
    BaseAdapterDelegate<Binding, List<Any>>() {

    override fun initBinding(binding: Binding) {
        super.initBinding(binding)
        binding.setVariable(BR.viewModel, viewModel)
    }
}