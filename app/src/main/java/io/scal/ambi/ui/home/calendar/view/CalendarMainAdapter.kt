package io.scal.ambi.ui.home.calendar.view

import android.databinding.ObservableList
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import io.scal.ambi.R
import io.scal.ambi.ui.global.base.adapter.DataObserverForAdapter

internal class CalendarMainAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var dataObserver: DataObserverForAdapter<UICalendarGroupDays>? = null

    fun getItem(position: Int): UICalendarGroupDays {
        return dataObserver!!.getItem(position)
    }

    override fun getItemCount(): Int = dataObserver?.getItemCount() ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.element_calendar_item_group, parent, false)
        return BaseViewHolder(view as CalendarWeekMonthView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as BaseViewHolder
        holder.view.setupDays(dataObserver!!.getItem(position))
    }

    fun updateData(data: ObservableList<UICalendarGroupDays>) {
        dataObserver?.release()

        dataObserver = DataObserverForAdapter(data, this)
        notifyDataSetChanged()
    }

    private class BaseViewHolder(internal val view: CalendarWeekMonthView) : RecyclerView.ViewHolder(view)
}