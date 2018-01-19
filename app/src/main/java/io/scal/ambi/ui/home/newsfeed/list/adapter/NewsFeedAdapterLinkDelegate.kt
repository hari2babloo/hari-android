package io.scal.ambi.ui.home.newsfeed.list.adapter

import com.ambi.work.R
import com.ambi.work.databinding.ItemNewsFeedTypeLinkBinding
import io.scal.ambi.ui.home.newsfeed.list.data.UIModelFeed

internal class NewsFeedAdapterLinkDelegate(viewModel: INewsFeedViewModel) :
    NewsFeedAdapterDelegateBase<ItemNewsFeedTypeLinkBinding>(viewModel) {

    override val layoutId: Int = R.layout.item_news_feed_type_link

    override fun isForViewType(items: List<Any>, position: Int): Boolean {
        return items[position] is UIModelFeed.Link
    }

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ItemNewsFeedTypeLinkBinding, payloads: MutableList<Any>) {
        binding.element = items[position] as? UIModelFeed.Link
        super.onBindViewHolder(items, position, binding, payloads)
    }
}