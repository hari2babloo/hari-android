package io.scal.ambi.ui.home.newsfeed.list

import io.scal.ambi.R
import io.scal.ambi.databinding.ItemNewsFeedMessageBinding

internal class NewsFeedAdapterMessageDelegate(viewModel: NewsFeedViewModel) :
    NewsFeedAdapterBaseDelegate<ItemNewsFeedMessageBinding>(viewModel) {

    override val layoutId: Int = R.layout.item_news_feed_message

    override fun isForViewType(items: List<Any>, position: Int): Boolean {
        return items[position] is ModelFeedElement.Message
    }

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ItemNewsFeedMessageBinding, payloads: MutableList<Any>) {
        binding.element = items[position] as? ModelFeedElement.Message
    }
}