package io.scal.ambi.ui.home.calendar.view

import android.databinding.ViewDataBinding
import android.view.ViewGroup
import io.scal.ambi.R
import io.scal.ambi.databinding.ItemCalendarDayWithNameBinding
import io.scal.ambi.ui.global.base.adapter.RecyclerViewAdapterBase

internal class CalendarGroupAdapter(private val groupDays: UICalendarGroupDays) : RecyclerViewAdapterBase() {

    override fun getItemCount(): Int = groupDays.days.size

    override fun onCreateBindingLayoutId(parent: ViewGroup, viewType: Int): Int = R.layout.item_calendar_day_with_name

    override fun onBindBinding(binding: ViewDataBinding, holder: RecyclerViewAdapterBase.BindingViewHolder<*>, position: Int) {
        val itemBinding = binding as ItemCalendarDayWithNameBinding
        itemBinding.event = groupDays.days[position]
        itemBinding.weekNameVisibility = groupDays is UICalendarGroupDays.Week || position < 7
    }
}