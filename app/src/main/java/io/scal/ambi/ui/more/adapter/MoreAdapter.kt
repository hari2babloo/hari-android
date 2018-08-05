package io.scal.ambi.ui.more.adapter

import io.scal.ambi.ui.global.base.adapter.HeaderFooterList
import io.scal.ambi.ui.global.base.adapter.RecyclerViewAdapterDelegated
import io.scal.ambi.ui.home.classes.MoreDelegate
import io.scal.ambi.ui.more.data.MoreData

class MoreAdapter(viewModel: IMoreItemViewModel) : RecyclerViewAdapterDelegated<Any>() {


    override var dataList: List<Any> = HeaderFooterList(null, null, emptyList())
    private var NotificationList: HeaderFooterList
        get() = dataList as HeaderFooterList
        set(value) {
            dataList = value
        }


    override fun getItemId(position: Int): Long {
        val item = dataList[position]
        return item.hashCode().toLong();
    }

    fun updateData(data: List<MoreData>) {
        dataList = data
        notifyDataSetChanged()
    }

    init {
        addDelegate(MoreDelegate(viewModel))
        setHasStableIds(true)
        NotificationList.updateHeaderVisibility(true, this)
        NotificationList.updateFooterVisibility(false, this)
    }


}