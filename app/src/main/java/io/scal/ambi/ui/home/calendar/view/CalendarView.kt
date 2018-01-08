package io.scal.ambi.ui.home.calendar.view

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.TypedValue
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
    private var currentMode: CalendarMode? = null

    private val calendarBinding by lazy { ElementCalendarViewBinding.inflate(LayoutInflater.from(context), this, false) }
    private lateinit var adapter: CalendarMainAdapter
    private var layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    private val scrollListener = object : RecyclerView.OnScrollListener() {

        private val handler = Handler(Looper.getMainLooper())
        private val currentDateCalculate: Runnable = object : Runnable {
            override fun run() {
                val firstAdapterPosition = layoutManager.findFirstVisibleItemPosition()
                val lastAdapterPosition = layoutManager.findLastVisibleItemPosition()
                if (firstAdapterPosition != lastAdapterPosition || adapter.itemCount == 0) {
                    handler.postDelayed(this, 10)
                    return
                }

                val selectedDay = currentSelectedDay
                if (null != selectedDay) {
                    val adapterItem = adapter.getItem(firstAdapterPosition)

                    if (!adapterItem.containsDate(selectedDay)) {
                        val newSelectedDay = adapterItem.days.firstOrNull { it.enabled }
                        newSelectedDay?.run { calendarBinding.viewModel?.setupDay(this.date) }
                    }
                }
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (RecyclerView.SCROLL_STATE_IDLE == newState) {
                handler.removeCallbacks(currentDateCalculate)
                currentDateCalculate.run()
            } else if (RecyclerView.SCROLL_STATE_SETTLING == newState) {
                handler.removeCallbacks(currentDateCalculate)
                handler.postDelayed(currentDateCalculate, 50)
            }
        }
    }

    init {
        addView(calendarBinding.root)
        calendarBinding.rvDates.isNestedScrollingEnabled = false
        calendarBinding.rvDates.layoutManager = layoutManager
        layoutManager.isItemPrefetchEnabled = true
        layoutManager.initialPrefetchItemCount = 2

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(calendarBinding.rvDates)
    }

    fun setupViewModel(calendarViewModel: CalendarViewModel) {
        adapter = CalendarMainAdapter(calendarViewModel)
        calendarBinding.rvDates.adapter = adapter
        calendarBinding.viewModel = calendarViewModel

        observeModel(calendarViewModel)
    }

    private fun observeModel(calendarViewModel: CalendarViewModel) {
        disposable?.run {
            calendarViewModel.observeSelectedDay()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { selectedDay ->
                    updateCurrentSelectedDay(selectedDay)

                    if (adapter.itemCount == 0) {
                        return@subscribe
                    }
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

            calendarViewModel.observeDataModeChange()
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    currentMode = it.first
                    prevHeight = null
                    requestLayout()

                    adapter.updateData(it.second)

                    val selectedDay = currentSelectedDay
                    if (null != selectedDay) {
                        val scrollToIndex = (0 until adapter.itemCount).firstOrNull { adapter.getItem(it).containsDate(selectedDay) }
                        scrollToIndex?.run { layoutManager.scrollToPosition(this) }
                    }
                }
                .addTo(this)
        }
    }

    private fun updateCurrentSelectedDay(selectedDay: LocalDate) {
        currentSelectedDay = selectedDay
        calendarBinding.selectedYearMonth = YearMonth(selectedDay)
        adapter.setSelectedDay(selectedDay)
    }

    private fun findCurrentAdapterPosition(): Int? {
        val firstItem = layoutManager.findFirstVisibleItemPosition()
        val lastItem = layoutManager.findLastVisibleItemPosition()
        if (firstItem != -1 && firstItem == lastItem) {
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

    private var prevWidth: Int? = null
    private var prevHeight: Int? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (null == currentMode) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        } else {
            val fullWidth = MeasureSpec.getSize(widthMeasureSpec)
            if (null != prevWidth && prevWidth == fullWidth && null != prevHeight) {
                super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(prevHeight!!, MeasureSpec.EXACTLY))
                return
            }

            val aboveRVSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 69f, context.resources.displayMetrics)
            val rvMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32f, context.resources.displayMetrics)
            val rvWidth = fullWidth - rvMargin
            val cellWidth = rvWidth / 7
            val weekNameHeight = cellWidth * 17 / 50 + TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, context.resources.displayMetrics)
            val dateHeight = cellWidth * 34 / 50 + TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, context.resources.displayMetrics)
            val rvHeight =
                when (currentMode) {
                    CalendarMode.WEEK  -> weekNameHeight + dateHeight
                    CalendarMode.MONTH -> weekNameHeight + dateHeight * 6
                    else               -> 0f
                }

            prevWidth = fullWidth
            prevHeight = Math.round(aboveRVSize + rvHeight)
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(prevHeight!!, MeasureSpec.EXACTLY))
        }
    }
}

private fun UICalendarGroupDays.containsDate(selectedDay: LocalDate): Boolean =
    days.firstOrNull { it.enabled && it.date == selectedDay } != null