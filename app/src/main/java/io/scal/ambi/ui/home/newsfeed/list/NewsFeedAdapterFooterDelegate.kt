package io.scal.ambi.ui.home.newsfeed.list

import io.scal.ambi.R
import io.scal.ambi.databinding.ItemNewsFeedFooterBinding

internal class NewsFeedAdapterFooterDelegate(private val footerElement: Any, viewModel: NewsFeedViewModel) :
    NewsFeedAdapterBaseDelegate<ItemNewsFeedFooterBinding>(viewModel) {

    override val layoutId: Int = R.layout.item_news_feed_footer

    override fun isForViewType(items: List<Any>, position: Int): Boolean =
        footerElement == items[position]

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ItemNewsFeedFooterBinding, payloads: MutableList<Any>) {
    }
}