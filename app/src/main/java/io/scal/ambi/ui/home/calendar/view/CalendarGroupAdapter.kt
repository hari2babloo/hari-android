package io.scal.ambi.ui.home.calendar.view

import android.databinding.ViewDataBinding
import android.view.ViewGroup
import io.scal.ambi.R
import io.scal.ambi.databinding.ItemCalendarDayWithNameBinding
import io.scal.ambi.ui.global.base.adapter.RecyclerViewAdapterBase

internal class CalendarGroupAdapter(private var groupDays: UICalendarGroupDays?) : RecyclerViewAdapterBase() {

    init {
        setHasStableIds(true)
    }

    override fun getItemCount(): Int = groupDays?.days?.size ?: 0

    override fun onCreateBindingLayoutId(parent: ViewGroup, viewType: Int): Int = R.layout.item_calendar_day_with_name

    override fun onBindBinding(binding: ViewDataBinding, holder: RecyclerViewAdapterBase.BindingViewHolder<*>, position: Int) {
        val itemBinding = binding as ItemCalendarDayWithNameBinding
        itemBinding.event = groupDays!!.days[position]
        itemBinding.weekNameVisibility = groupDays is UICalendarGroupDays.Week || position < 7
    }

    internal fun updateGroupDays(groupDays: UICalendarGroupDays) {
        this.groupDays = groupDays
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return groupDays!!.days[position].date.dayOfYear.toLong()
    }
}