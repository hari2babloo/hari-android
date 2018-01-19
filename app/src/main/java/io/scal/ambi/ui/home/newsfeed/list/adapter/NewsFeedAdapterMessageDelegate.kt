package io.scal.ambi.ui.home.newsfeed.list.adapter

import com.ambi.work.R
import com.ambi.work.databinding.ItemNewsFeedTypeMessageBinding
import io.scal.ambi.ui.home.newsfeed.list.data.UIModelFeed
import io.scal.ambi.ui.home.newsfeed.list.NewsFeedViewModel

internal class NewsFeedAdapterMessageDelegate(viewModel: INewsFeedViewModel) :
    NewsFeedAdapterDelegateBase<ItemNewsFeedTypeMessageBinding>(viewModel) {

    override val layoutId: Int = R.layout.item_news_feed_type_message

    override fun isForViewType(items: List<Any>, position: Int): Boolean {
        return items[position] is UIModelFeed.Message
    }

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ItemNewsFeedTypeMessageBinding, payloads: MutableList<Any>) {
        binding.element = items[position] as? UIModelFeed.Message
        super.onBindViewHolder(items, position, binding, payloads)
    }
}