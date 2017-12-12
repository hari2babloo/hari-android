package io.scal.ambi.ui.home.newsfeed.audience

import android.databinding.ViewDataBinding
import android.view.ViewGroup
import io.scal.ambi.BR
import io.scal.ambi.R
import io.scal.ambi.ui.global.base.adapter.BaseRecyclerViewAdapter

internal class AudienceSelectionAdapter(private val viewModel: AudienceSelectionViewModel) : BaseRecyclerViewAdapter() {

    override fun onCreateBindingLayoutId(parent: ViewGroup, viewType: Int): Int =
        R.layout.item_audience_selection

    override fun onBindBinding(binding: ViewDataBinding, holder: BindingViewHolder<*>, position: Int) {
        binding.setVariable(BR.item, viewModel.audienceList[position])
        binding.setVariable(BR.viewModel, viewModel)
    }

    override fun getItemCount(): Int =
        viewModel.audienceList.size
}