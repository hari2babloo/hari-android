package io.scal.ambi.ui.home.newsfeed.list.adapter

import com.ambi.work.R
import com.ambi.work.databinding.ItemNewsFeedTypePollBinding
import io.scal.ambi.ui.home.newsfeed.list.data.UIModelFeed

internal class NewsFeedAdapterPollDelegate(viewModel: INewsFeedViewModel) :
    NewsFeedAdapterDelegateBase<ItemNewsFeedTypePollBinding>(viewModel) {

    override val layoutId: Int = R.layout.item_news_feed_type_poll

    override fun isForViewType(items: List<Any>, position: Int): Boolean {
        return items[position] is UIModelFeed.Poll
    }

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ItemNewsFeedTypePollBinding, payloads: MutableList<Any>) {
        binding.element = items[position] as? UIModelFeed.Poll
        super.onBindViewHolder(items, position, binding, payloads)
    }
}