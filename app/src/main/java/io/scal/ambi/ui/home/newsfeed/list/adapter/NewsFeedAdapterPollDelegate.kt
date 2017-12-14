package io.scal.ambi.ui.home.newsfeed.list.adapter

import io.scal.ambi.R
import io.scal.ambi.databinding.ItemNewsFeedTypePollBinding
import io.scal.ambi.ui.home.newsfeed.list.data.ElementModelFeed
import io.scal.ambi.ui.home.newsfeed.list.NewsFeedViewModel

internal class NewsFeedAdapterPollDelegate(viewModel: NewsFeedViewModel) :
    NewsFeedAdapterBaseDelegate<ItemNewsFeedTypePollBinding>(viewModel) {

    override val layoutId: Int = R.layout.item_news_feed_type_poll

    override fun isForViewType(items: List<Any>, position: Int): Boolean {
        return items[position] is ElementModelFeed.Poll
    }

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ItemNewsFeedTypePollBinding, payloads: MutableList<Any>) {
        binding.element = items[position] as? ElementModelFeed.Poll
    }
}