package io.scal.ambi.ui.home.newsfeed.list.adapter

import android.databinding.ObservableList
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates3.AdapterDelegatesManager
import io.scal.ambi.ui.global.base.adapter.BaseAdapterDataObserver
import io.scal.ambi.ui.home.newsfeed.list.ElementModelFeed
import io.scal.ambi.ui.home.newsfeed.list.NewsFeedViewModel

class NewsFeedAdapter(viewModel: NewsFeedViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val headerElement = Any()
    private val footerElement = Any()

    private var dataObserver: NewsFeedAdapterDataObserver? = null
    private var headerFeedList = HeaderFooterFeedList(headerElement, footerElement, emptyList())

    private val delegatesManager = AdapterDelegatesManager<List<Any>>()

    init {
        delegatesManager.addDelegate(NewsFeedAdapterHeaderDelegate(headerElement, viewModel))
        delegatesManager.addDelegate(NewsFeedAdapterMessageDelegate(viewModel))
        delegatesManager.addDelegate(NewsFeedAdapterPollDelegate(viewModel))
        delegatesManager.addDelegate(NewsFeedAdapterLinkDelegate(viewModel))
        delegatesManager.addDelegate(NewsFeedAdapterFooterDelegate(footerElement, viewModel))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        delegatesManager.onCreateViewHolder(parent, viewType)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegatesManager.onBindViewHolder(headerFeedList, position, holder)
    }

    override fun getItemCount(): Int =
        headerFeedList.size

    override fun getItemViewType(position: Int): Int =
        delegatesManager.getItemViewType(headerFeedList, position)

    fun updateData(data: ObservableList<ElementModelFeed>) {
        releaseData()
        headerFeedList = headerFeedList.copy(feedData = data)
        dataObserver = NewsFeedAdapterDataObserver(data, this)
        notifyDataSetChanged()
    }

    fun releaseData() {
        dataObserver?.release()
        headerFeedList = headerFeedList.copy(feedData = emptyList())
        dataObserver = null
        notifyDataSetChanged()
    }

    fun showPageProgress(show: Boolean) {
        headerFeedList.updatePageProgress(show, this)
    }

    private data class HeaderFooterFeedList(private val headerElement: Any,
                                            private val footerElement: Any,
                                            private val feedData: List<ElementModelFeed>) : AbstractList<Any>() {

        private var showPageProgress: Boolean = false

        override val size: Int
            get() = feedData.size + 1 + (if (showPageProgress) 1 else 0)

        override fun get(index: Int): Any =
            if (0 == index) {
                headerElement
            } else if (showPageProgress && size - 1 == index) {
                footerElement
            } else {
                feedData[index - 1]
            }

        fun updatePageProgress(show: Boolean, adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
            if (showPageProgress != show) {
                showPageProgress = show
                if (show) {
                    adapter.notifyItemInserted(size - 1)
                } else {
                    adapter.notifyItemRemoved(size + 1)
                }
            }
        }
    }

    private class NewsFeedAdapterDataObserver(data: ObservableList<ElementModelFeed>, adapter: RecyclerView.Adapter<*>) :
        BaseAdapterDataObserver<ElementModelFeed>(data, adapter) {

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