package io.scal.ambi.ui.home.newsfeed.list.adapter

import android.databinding.ObservableList
import android.support.v7.widget.RecyclerView
import io.scal.ambi.R
import io.scal.ambi.ui.global.base.adapter.AdapterDelegateStaticView
import io.scal.ambi.ui.global.base.adapter.DataObserverForAdapter
import io.scal.ambi.ui.global.base.adapter.HeaderFooterList
import io.scal.ambi.ui.global.base.adapter.RecyclerViewAdapterDelegated
import io.scal.ambi.ui.home.newsfeed.list.NewsFeedViewModel
import io.scal.ambi.ui.home.newsfeed.list.data.UIModelFeed

class NewsFeedAdapter(viewModel: NewsFeedViewModel) : RecyclerViewAdapterDelegated<Any>() {

    private val headerElement = Any()
    private val footerElement = Any()

    private var dataObserver: NewsFeedAdapterDataObserver? = null

    override var dataList: List<Any> = HeaderFooterList(headerElement, footerElement, emptyList())
    private var newsFeedList: HeaderFooterList
        get() = dataList as HeaderFooterList
        set(value) {
            dataList = value
        }

    init {
        addDelegate(NewsFeedAdapterHeaderDelegate(headerElement, viewModel))
        addDelegate(NewsFeedAdapterMessageDelegate(viewModel))
        addDelegate(NewsFeedAdapterPollDelegate(viewModel))
        addDelegate(NewsFeedAdapterLinkDelegate(viewModel))
        addDelegate(AdapterDelegateStaticView(footerElement, R.layout.item_adapter_progress_footer))

        setHasStableIds(true)

        newsFeedList.updateHeaderVisibility(true, this)
        newsFeedList.updateFooterVisibility(false, this)
    }

    override fun getItemId(position: Int): Long {
        val item = newsFeedList[position]
        return when (item) {
            headerElement  -> "header_0".hashCode().toLong()
            is UIModelFeed -> item.uid.hashCode().toLong()
            footerElement  -> "footer_0".hashCode().toLong()
            else           -> throw IllegalArgumentException("unknown item: $item")
        }
    }

    fun updateData(data: ObservableList<UIModelFeed>) {
        releaseData()
        newsFeedList = newsFeedList.copy(data = data)
        dataObserver = NewsFeedAdapterDataObserver(data, this)
        notifyDataSetChanged()
    }

    fun releaseData() {
        dataObserver?.release()
        newsFeedList = newsFeedList.copy(data = emptyList())
        dataObserver = null
        notifyDataSetChanged()
    }

    fun showPageProgress(show: Boolean) {
        newsFeedList.updateFooterVisibility(show, this)
    }

    private class NewsFeedAdapterDataObserver(data: ObservableList<UIModelFeed>, adapter: RecyclerView.Adapter<*>) :
        DataObserverForAdapter<UIModelFeed>(data, adapter) {

        override fun notifyItemRangeChanged(adapter: RecyclerView.Adapter<*>, positionStart: Int, itemCount: Int) {
            super.notifyItemRangeChanged(adapter, positionStart + 1, itemCount)
        }

        override fun notifyItemRangeInserted(adapter: RecyclerView.Adapter<*>, toPosition: Int, itemCount: Int) {
            super.notifyItemRangeInserted(adapter, toPosition + 1, itemCount)
        }

        override fun notifyItemRangeRemoved(adapter: RecyclerView.Adapter<*>, fromPosition: Int, itemCount: Int) {
            super.notifyItemRangeRemoved(adapter, fromPosition + 1, itemCount)
        }
    }
}