package io.scal.ambi.ui.home.newsfeed

import io.scal.ambi.R
import io.scal.ambi.databinding.ItemNewsFeedLinkBinding

internal class NewsFeedAdapterLinkDelegate(viewModel: NewsFeedViewModel) :
    NewsFeedAdapterBaseDelegate<ItemNewsFeedLinkBinding>(viewModel) {

    override val layoutId: Int = R.layout.item_news_feed_link

    override fun isForViewType(items: List<Any>, position: Int): Boolean {
        return items[position] is ModelFeedElement.Link
    }

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ItemNewsFeedLinkBinding, payloads: MutableList<Any>) {
        binding.element = items[position] as? ModelFeedElement.Link
    }
}