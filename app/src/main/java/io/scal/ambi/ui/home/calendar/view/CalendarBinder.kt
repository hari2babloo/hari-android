package io.scal.ambi.ui.home.calendar.view

import android.annotation.SuppressLint
import android.databinding.BindingAdapter
import android.support.v7.widget.AppCompatImageView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.ambi.work.R
import org.joda.time.LocalDate
import org.joda.time.YearMonth
import org.joda.time.format.DateTimeFormat
import java.lang.ref.WeakReference
import java.util.*

object CalendarBinder {

    private val maxEventsCount = 3
    private val eventsViewsCache = mutableListOf<WeakReference<AppCompatImageView>>()

    @JvmStatic
    @BindingAdapter("calendarDayEvents")
    fun bindDayEvents(viewGroup: ViewGroup, events: UICalendarEvents?) {
        removeAllEventsViews(viewGroup)

        if (null != events) {
            if (events.colors.size > maxEventsCount) {
                (0 until maxEventsCount)
                    .forEach { index -> addEventView(viewGroup, events.colors[index], index == maxEventsCount - 1) }
            } else {
                events.colors.forEach { addEventView(viewGroup, it, false) }
            }
        }
    }

    private fun addEventView(viewGroup: ViewGroup, color: Int, more: Boolean) {
        val view = getEventsView(viewGroup)
        view.setImageResource(if (more) R.drawable.ic_calendar_day_event_more else R.drawable.ic_calendar_day_event_simple)
        view.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN)
        viewGroup.addView(view)
    }

    private fun getEventsView(viewGroup: ViewGroup): AppCompatImageView {
        val layoutInflater by lazy { LayoutInflater.from(viewGroup.context) }
        val iterator = eventsViewsCache.iterator()
        while (iterator.hasNext()) {
            val eventsBindingRef = iterator.next()
            val eventsBinding = eventsBindingRef.get()
            iterator.remove()
            if (null != eventsBinding) {
                return eventsBinding
            }
        }
        return layoutInflater.inflate(R.layout.item_calendar_day_event, viewGroup, false) as AppCompatImageView
    }

    private fun removeAllEventsViews(viewGroup: ViewGroup) {
        if (viewGroup.childCount > 0) {
            (0 until viewGroup.childCount)
                .map { viewGroup.getChildAt(it) }
                .map { it as AppCompatImageView }
                .forEach {
                    viewGroup.removeView(it)
                    eventsViewsCache.add(WeakReference(it))
                }
        }
    }

    private val dayOfWeekFormatter = DateTimeFormat.forPattern("E").withLocale(Locale.ENGLISH)
    private val monthFormatter = DateTimeFormat.forPattern("MMMM").withLocale(Locale.ENGLISH)
    private val yearFormatter = DateTimeFormat.forPattern("yyyy").withLocale(Locale.ENGLISH)

    @JvmStatic
    @BindingAdapter("calendarWeekDayName")
    fun bindWeekDayName(textView: TextView, day: LocalDate?) {
        val dayOfWeek = day?.let { dayOfWeekFormatter.print(it) } ?: ""
        textView.text = dayOfWeek.firstOrNull()?.toString().orEmpty().toUpperCase()
    }

    @SuppressLint("SetTextI18n")
    @JvmStatic
    @BindingAdapter("calendarMonth")
    fun bindMonthName(textView: TextView, yearMonth: YearMonth?) {
        val monthName = yearMonth?.let { monthFormatter.print(it) }?.toLowerCase() ?: ""
        if (monthName.length > 1) {
            textView.text = monthName.first().toUpperCase().toString() + monthName.substring(1)
        } else {
            textView.text = monthName
        }
    }

    @JvmStatic
    @BindingAdapter("calendarYear")
    fun bindYearName(textView: TextView, yearMonth: YearMonth?) {
        val year = yearMonth?.year
        textView.text = year?.toString() ?: ""
    }
}