package io.scal.ambi.ui.home.newsfeed.list.adapter

import io.scal.ambi.R
import io.scal.ambi.databinding.ItemNewsFeedHeaderBinding
import io.scal.ambi.ui.home.newsfeed.list.NewsFeedViewModel

internal class NewsFeedAdapterHeaderDelegateBase(private val headerElement: Any, viewModel: NewsFeedViewModel) :
    NewsFeedAdapterDelegateBase<ItemNewsFeedHeaderBinding>(viewModel) {

    override val layoutId: Int = R.layout.item_news_feed_header

    override fun isForViewType(items: List<Any>, position: Int): Boolean =
        headerElement == items[position]
}