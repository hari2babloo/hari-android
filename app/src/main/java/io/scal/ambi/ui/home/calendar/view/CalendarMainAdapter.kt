package io.scal.ambi.ui.home.calendar.view

import android.databinding.ObservableList
import android.databinding.ViewDataBinding
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import io.scal.ambi.R
import io.scal.ambi.databinding.ElementCalendarItemGroupBinding
import io.scal.ambi.ui.global.base.adapter.DataObserverForAdapter
import io.scal.ambi.ui.global.base.adapter.RecyclerViewAdapterBase

internal class CalendarMainAdapter : RecyclerViewAdapterBase() {

    private val innerRecyclerViewPoll = RecyclerView.RecycledViewPool()

    private var dataObserver: DataObserverForAdapter<UICalendarGroupDays>? = null

    fun getItem(position: Int): UICalendarGroupDays {
        return dataObserver!!.getItem(position)
    }

    override fun getItemCount(): Int = dataObserver?.getItemCount() ?: 0

    override fun onCreateBindingLayoutId(parent: ViewGroup, viewType: Int): Int = R.layout.element_calendar_item_group

    override fun onCreateBinding(binding: ViewDataBinding, viewType: Int) {
        super.onCreateBinding(binding, viewType)
        binding as ElementCalendarItemGroupBinding
        binding.rvDateGroup.recycledViewPool = innerRecyclerViewPoll
        binding.rvDateGroup.layoutManager = GridLayoutManager(binding.root.context, 7, GridLayoutManager.VERTICAL, false)
        binding.rvDateGroup.adapter = CalendarGroupAdapter(null)
    }

    override fun onBindBinding(binding: ViewDataBinding, holder: RecyclerViewAdapterBase.BindingViewHolder<*>, position: Int) {
        binding as ElementCalendarItemGroupBinding
        (binding.rvDateGroup.adapter as CalendarGroupAdapter).updateGroupDays(dataObserver!!.getItem(position))
    }

    fun updateData(data: ObservableList<UICalendarGroupDays>) {
        dataObserver?.release()

        dataObserver = DataObserverForAdapter(data, this)
        notifyDataSetChanged()
    }
}