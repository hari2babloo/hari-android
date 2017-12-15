package io.scal.ambi.ui.home.newsfeed.list.adapter

import io.scal.ambi.R
import io.scal.ambi.databinding.ItemNewsFeedTypeLinkBinding
import io.scal.ambi.ui.home.newsfeed.list.data.ElementModelFeed
import io.scal.ambi.ui.home.newsfeed.list.NewsFeedViewModel

internal class NewsFeedAdapterLinkDelegateBase(viewModel: NewsFeedViewModel) :
    NewsFeedAdapterDelegateBase<ItemNewsFeedTypeLinkBinding>(viewModel) {

    override val layoutId: Int = R.layout.item_news_feed_type_link

    override fun isForViewType(items: List<Any>, position: Int): Boolean {
        return items[position] is ElementModelFeed.Link
    }

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ItemNewsFeedTypeLinkBinding, payloads: MutableList<Any>) {
        binding.element = items[position] as? ElementModelFeed.Link
    }
}