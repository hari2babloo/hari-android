package io.scal.ambi.ui.home.calendar.view

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.databinding.ElementCalendarViewBinding
import org.joda.time.LocalDate
import org.joda.time.YearMonth


class CalendarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private var disposable: CompositeDisposable? = null
    private var currentSelectedDay: LocalDate? = null

    private val calendarBinding = ElementCalendarViewBinding.inflate(LayoutInflater.from(context), this, false)
    private val adapter = CalendarMainAdapter()
    private var layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (RecyclerView.SCROLL_STATE_IDLE == newState || RecyclerView.SCROLL_STATE_SETTLING == newState) {
                val currentAdapterPosition = layoutManager.findFirstVisibleItemPosition()
                val selectedDay = currentSelectedDay
                if (null != selectedDay) {
                    val adapterItem = adapter.getItem(currentAdapterPosition)

                    if (!adapterItem.containsDate(selectedDay)) {
                        val newSelectedDay = adapterItem.days.firstOrNull { it.enabled }
                        newSelectedDay?.run { calendarBinding.viewModel?.setupDay(this.date) }
                    }
                }
            }
        }
    }

    init {
        addView(calendarBinding.root)
        calendarBinding.rvDates.isNestedScrollingEnabled = false
        calendarBinding.rvDates.layoutManager = layoutManager
        calendarBinding.rvDates.adapter = adapter

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(calendarBinding.rvDates)
    }

    fun setupViewModel(calendarViewModel: CalendarViewModel) {
        calendarBinding.viewModel = calendarViewModel

        adapter.updateData(calendarViewModel.listOfData)

        observeModel(calendarViewModel)
    }

    private fun observeModel(calendarViewModel: CalendarViewModel) {
        disposable?.run {
            calendarViewModel.observeSelectedDay()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { selectedDay ->
                    calendarBinding.selectedYearMonth = YearMonth(selectedDay)
                    currentSelectedDay = selectedDay

                    val currentPosition = findCurrentAdapterPosition()
                    if (null != currentPosition && adapter.getItem(currentPosition).containsDate(selectedDay)) {
                        return@subscribe
                    }
                    if (calendarBinding.rvDates.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                        val scrollToIndex = (0 until adapter.itemCount).firstOrNull { adapter.getItem(it).containsDate(selectedDay) }
                        scrollToIndex?.run { layoutManager.scrollToPosition(this) }
                    }

                }
                .addTo(this)
        }
    }

    private fun findCurrentAdapterPosition(): Int? {
        val firstItem = layoutManager.findFirstVisibleItemPosition()
        val lastItem = layoutManager.findLastVisibleItemPosition()
        if (firstItem == lastItem) {
            return firstItem
        }
        return null
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        disposable?.dispose()
        disposable = CompositeDisposable()

        calendarBinding.rvDates.addOnScrollListener(scrollListener)
        calendarBinding.viewModel?.run { observeModel(this) }
    }

    override fun onDetachedFromWindow() {
        calendarBinding.rvDates.removeOnScrollListener(scrollListener)
        disposable?.dispose()
        disposable = null

        super.onDetachedFromWindow()
    }
}

private fun UICalendarGroupDays.containsDate(selectedDay: LocalDate): Boolean =
    days.firstOrNull { it.enabled && it.date == selectedDay } != null