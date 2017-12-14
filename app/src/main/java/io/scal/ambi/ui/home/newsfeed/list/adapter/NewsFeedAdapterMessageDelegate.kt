package io.scal.ambi.ui.home.newsfeed.list.adapter

import io.scal.ambi.R
import io.scal.ambi.databinding.ItemNewsFeedTypeMessageBinding
import io.scal.ambi.ui.home.newsfeed.list.data.ElementModelFeed
import io.scal.ambi.ui.home.newsfeed.list.NewsFeedViewModel

internal class NewsFeedAdapterMessageDelegate(viewModel: NewsFeedViewModel) :
    NewsFeedAdapterBaseDelegate<ItemNewsFeedTypeMessageBinding>(viewModel) {

    override val layoutId: Int = R.layout.item_news_feed_type_message

    override fun isForViewType(items: List<Any>, position: Int): Boolean {
        return items[position] is ElementModelFeed.Message
    }

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ItemNewsFeedTypeMessageBinding, payloads: MutableList<Any>) {
        binding.element = items[position] as? ElementModelFeed.Message
    }
}