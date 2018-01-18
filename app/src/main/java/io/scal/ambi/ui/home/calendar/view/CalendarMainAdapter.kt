package io.scal.ambi.ui.home.calendar.view

import android.databinding.ObservableList
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ambi.work.R
import io.scal.ambi.ui.global.base.adapter.DataObserverForAdapter
import org.joda.time.LocalDate

open class CalendarMainAdapter(private val viewModel: CalendarViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var dataObserver: DataObserverForAdapter<UICalendarGroupDays>? = null

    private var selectedDay: LocalDate? = null
    private var cachedEvents: Map<LocalDate, UICalendarEvents?> = emptyMap()

    internal fun getItem(position: Int): UICalendarGroupDays {
        return dataObserver!!.getItem(position)
    }

    override fun getItemCount(): Int = dataObserver?.getItemCount() ?: 0

    override final fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.element_calendar_item_group, parent, false)
        view as CalendarWeekMonthView
        view.setup(viewModel)
        return BaseViewHolder(view)
    }

    override final fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as BaseViewHolder
        holder.view.updateDays(dataObserver!!.getItem(position), selectedDay, cachedEvents)
    }

    internal fun setSelectedDay(selectedDay: LocalDate) {
        this.selectedDay = selectedDay
        notifyDataUpdated()
    }

    fun updateEvent(events: Map<LocalDate, UICalendarEvents?>) {
        cachedEvents = events
        notifyDataUpdated()
    }

    internal fun updateData(data: ObservableList<UICalendarGroupDays>) {
        dataObserver?.release()

        dataObserver = DataObserverForAdapter(data, this)
        notifyDataSetChanged()
    }

    internal fun notifyDataUpdated() {
        (0 until itemCount).forEach { notifyItemChanged(it) }
    }

    private class BaseViewHolder(internal val view: CalendarWeekMonthView) : RecyclerView.ViewHolder(view)
}