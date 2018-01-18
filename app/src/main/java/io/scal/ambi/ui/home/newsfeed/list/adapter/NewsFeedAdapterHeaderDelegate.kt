package io.scal.ambi.ui.home.newsfeed.list.adapter

import com.ambi.work.R
import com.ambi.work.databinding.ItemNewsFeedHeaderBinding
import io.scal.ambi.ui.home.newsfeed.list.NewsFeedViewModel

internal class NewsFeedAdapterHeaderDelegate(private val headerElement: Any, viewModel: NewsFeedViewModel) :
    NewsFeedAdapterDelegateBase<ItemNewsFeedHeaderBinding>(viewModel) {

    override val layoutId: Int = R.layout.item_news_feed_header

    override fun isForViewType(items: List<Any>, position: Int): Boolean =
        headerElement == items[position]
}