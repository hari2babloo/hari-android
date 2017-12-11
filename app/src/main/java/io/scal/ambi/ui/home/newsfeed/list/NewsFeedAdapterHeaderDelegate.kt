package io.scal.ambi.ui.home.newsfeed.list

import io.scal.ambi.R
import io.scal.ambi.databinding.ItemNewsFeedHeaderBinding

internal class NewsFeedAdapterHeaderDelegate(private val headerElement: Any, viewModel: NewsFeedViewModel) :
    NewsFeedAdapterBaseDelegate<ItemNewsFeedHeaderBinding>(viewModel) {

    override val layoutId: Int = R.layout.item_news_feed_header

    override fun isForViewType(items: List<Any>, position: Int): Boolean =
        headerElement == items[position]

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ItemNewsFeedHeaderBinding, payloads: MutableList<Any>) {
    }
}