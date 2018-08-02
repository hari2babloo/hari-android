package io.scal.ambi.ui.home.notifications

import android.databinding.ObservableList
import android.support.v7.widget.RecyclerView
import io.scal.ambi.ui.global.base.adapter.DataObserverForAdapter
import io.scal.ambi.ui.global.base.adapter.HeaderFooterList
import io.scal.ambi.ui.global.base.adapter.RecyclerViewAdapterDelegated

/**
 * Created by chandra on 30-07-2018.
 */

class NotificationAdapter(viewModel: INotificationViewModel) : RecyclerViewAdapterDelegated<Any>(){

    private val headerElement = Any()
    private val footerElement = Any()

    private var dataObserver: NotificationAdapterDataObserver? = null

    private val hasHeader = viewModel is NotificationsViewModel
    override var dataList: List<Any> = HeaderFooterList(null,null,emptyList())
    private var NotificationList: HeaderFooterList
        get() = dataList as HeaderFooterList
        set(value) {
            dataList = value
        }

    init {
        addDelegate(NotificationDelegate(viewModel))
        setHasStableIds(true)
        NotificationList.updateHeaderVisibility(true, this)
        NotificationList.updateFooterVisibility(false, this)
    }

    override fun getItemId(position: Int): Long {
        val item = NotificationList[position]
        return item.hashCode().toLong();
    }

    fun updateData(data: ObservableList<NotificationData>) {
        releaseData()
        NotificationList = NotificationList.copy(data = data)
        dataObserver = NotificationAdapterDataObserver(data, this, hasHeader)
        notifyDataSetChanged()
    }

    fun releaseData() {
        dataObserver?.release()
        NotificationList = NotificationList.copy(data = emptyList())
        dataObserver = null
        notifyDataSetChanged()
    }

    fun showPageProgress(show: Boolean) {
        NotificationList.updateFooterVisibility(show, this)
    }

    private class NotificationAdapterDataObserver(data: ObservableList<NotificationData>, adapter: RecyclerView.Adapter<*>, private val hasHeader: Boolean) :
            DataObserverForAdapter<NotificationData>(data, adapter) {

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