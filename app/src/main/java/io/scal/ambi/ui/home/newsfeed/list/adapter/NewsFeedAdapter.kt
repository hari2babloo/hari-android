package io.scal.ambi.ui.home.newsfeed.list.adapter

import android.databinding.ObservableList
import android.support.v7.widget.RecyclerView
import com.ambi.work.R
import io.scal.ambi.ui.global.base.adapter.AdapterDelegateStaticView
import io.scal.ambi.ui.global.base.adapter.DataObserverForAdapter
import io.scal.ambi.ui.global.base.adapter.HeaderFooterList
import io.scal.ambi.ui.global.base.adapter.RecyclerViewAdapterDelegated
import io.scal.ambi.ui.home.newsfeed.list.NewsFeedViewModel
import io.scal.ambi.ui.home.newsfeed.list.data.UIModelFeed

class NewsFeedAdapter(viewModel: INewsFeedViewModel) : RecyclerViewAdapterDelegated<Any>() {

    private val headerElement = Any()
    private val footerElement = Any()

    private var dataObserver: NewsFeedAdapterDataObserver? = null

    private val hasHeader = viewModel is NewsFeedViewModel
    override var dataList: List<Any> = HeaderFooterList(if (hasHeader) headerElement else null, footerElement, emptyList())
    private var newsFeedList: HeaderFooterList
        get() = dataList as HeaderFooterList
        set(value) {
            dataList = value
        }

    init {
        if (hasHeader) {
            addDelegate(NewsFeedAdapterHeaderDelegate(headerElement, viewModel as NewsFeedViewModel))
        }
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
        dataObserver = NewsFeedAdapterDataObserver(data, this, hasHeader)
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

    private class NewsFeedAdapterDataObserver(data: ObservableList<UIModelFeed>, adapter: RecyclerView.Adapter<*>, private val hasHeader: Boolean) :
        DataObserverForAdapter<UIModelFeed>(data, adapter) {

        override fun notifyItemRangeChanged(adapter: RecyclerView.Adapter<*>, positionStart: Int, itemCount: Int) {
            super.notifyItemRangeChanged(adapter, positionStart + (if (hasHeader) 1 else 0), itemCount)
        }

        override fun notifyItemRangeInserted(adapter: RecyclerView.Adapter<*>, toPosition: Int, itemCount: Int) {
            super.notifyItemRangeInserted(adapter, toPosition + (if (hasHeader) 1 else 0), itemCount)
        }

        override fun notifyItemRangeRemoved(adapter: RecyclerView.Adapter<*>, fromPosition: Int, itemCount: Int) {
            super.notifyItemRangeRemoved(adapter, fromPosition + (if (hasHeader) 1 else 0), itemCount)
        }
    }
}