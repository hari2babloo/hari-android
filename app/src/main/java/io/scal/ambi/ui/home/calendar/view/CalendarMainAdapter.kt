package io.scal.ambi.ui.home.calendar.view

import android.databinding.ObservableList
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import io.scal.ambi.R
import io.scal.ambi.ui.global.base.adapter.DataObserverForAdapter
import org.joda.time.LocalDate

internal class CalendarMainAdapter(private val viewModel: CalendarViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var dataObserver: DataObserverForAdapter<UICalendarGroupDays>? = null
    private var selectedDay: LocalDate? = null

    fun getItem(position: Int): UICalendarGroupDays {
        return dataObserver!!.getItem(position)
    }

    override fun getItemCount(): Int = dataObserver?.getItemCount() ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.element_calendar_item_group, parent, false)
        view as CalendarWeekMonthView
        view.setup(viewModel)
        return BaseViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as BaseViewHolder
        holder.view.updateDays(dataObserver!!.getItem(position), selectedDay)
    }

    fun setSelectedDay(selectedDay: LocalDate) {
        this.selectedDay = selectedDay
        (0 until itemCount).forEach { notifyItemChanged(it) }
    }

    fun updateData(data: ObservableList<UICalendarGroupDays>) {
        dataObserver?.release()

        dataObserver = DataObserverForAdapter(data, this)
        notifyDataSetChanged()
    }

    private class BaseViewHolder(internal val view: CalendarWeekMonthView) : RecyclerView.ViewHolder(view)
}