package io.scal.ambi.ui.home.newsfeed

import android.databinding.ObservableList
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates3.AdapterDelegatesManager
import io.scal.ambi.ui.global.base.adapter.BaseAdapterDataObserver

internal class NewsFeedAdapter(viewModel: NewsFeedViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val headerElement = Any()

    private var dataObserver: BaseAdapterDataObserver<ModelFeedElement>? = null
    private var headerFeedList = HeaderFeedList(headerElement, emptyList())

    private val delegatesManager = AdapterDelegatesManager<List<Any>>()

    init {
        delegatesManager.addDelegate(NewsFeedAdapterHeaderDelegate(headerElement, viewModel))
        delegatesManager.addDelegate(NewsFeedAdapterMessageDelegate(viewModel))
        delegatesManager.addDelegate(NewsFeedAdapterLinkDelegate(viewModel))
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

    fun updateData(data: ObservableList<ModelFeedElement>) {
        releaseData()
        headerFeedList = HeaderFeedList(headerElement, data)
        dataObserver = BaseAdapterDataObserver(data, this)
        notifyDataSetChanged()
    }

    fun releaseData() {
        dataObserver?.release()
        headerFeedList = HeaderFeedList(headerElement, emptyList())
        dataObserver = null
        notifyDataSetChanged()
    }

    private class HeaderFeedList(private val headerElement: Any, private val feedData: List<ModelFeedElement>) : AbstractList<Any>() {

        override val size: Int
            get() = feedData.size + 1

        override fun get(index: Int): Any =
            if (0 == index) {
                headerElement
            } else {
                feedData[index - 1]
            }
    }
}