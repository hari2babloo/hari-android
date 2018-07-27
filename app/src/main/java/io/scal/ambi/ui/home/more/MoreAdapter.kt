package io.scal.ambi.ui.home.more

import com.ambi.work.R
import io.scal.ambi.ui.global.base.adapter.AdapterDelegateStaticView
import io.scal.ambi.ui.global.base.adapter.HeaderFooterList
import io.scal.ambi.ui.global.base.adapter.RecyclerViewAdapterDelegated

/**
 * Created by chandra on 23-07-2018.
 */

class MoreAdapter(viewModel: IMoreItemViewModel): RecyclerViewAdapterDelegated<Any>(){
    private val footerElement = Any()
    override var dataList: List<Any> = HeaderFooterList(null, footerElement, emptyList())
    private var chatList: HeaderFooterList
        get() = dataList as HeaderFooterList
        set(value) {
            dataList = value
        }

    fun updateData(data: List<MoreItem>) {
        chatList = chatList.copy(data = data)
        dataList = chatList
        notifyDataSetChanged()
    }

    init {
        addDelegate(MoreAdapterDelegate(viewModel))

        setHasStableIds(true)

        chatList.updateFooterVisibility(false, this)
        addDelegate(AdapterDelegateStaticView(footerElement, R.layout.item_adapter_progress_footer))
    }

    override fun getItemId(position: Int): Long {
        val item = chatList[position]
        return item.hashCode().toLong();

    }

}