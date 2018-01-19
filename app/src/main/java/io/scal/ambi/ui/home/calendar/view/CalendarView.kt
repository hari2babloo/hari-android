package io.scal.ambi.ui.home.calendar.view

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import com.ambi.work.databinding.ElementCalendarViewBinding
import org.joda.time.LocalDate
import org.joda.time.YearMonth


class CalendarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    private var disposable: CompositeDisposable? = null

    private var currentSelectedDay: LocalDate? = null

    private val calendarBinding by lazy { ElementCalendarViewBinding.inflate(LayoutInflater.from(context), this, false) }
    private var recyclerView: CalendarFixedSizeRecyclerView? = null
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
        orientation = VERTICAL
        super.addView(calendarBinding.root)
        layoutManager.isItemPrefetchEnabled = true
        layoutManager.initialPrefetchItemCount = 2
    }

    fun setupViewModel(calendarViewModel: CalendarViewModel,
                       recyclerView: CalendarFixedSizeRecyclerView,
                       calendarAdapter: CalendarMainAdapter = CalendarMainAdapter(calendarViewModel)) {
        adapter = calendarAdapter

        recyclerView.isNestedScrollingEnabled = false
        recyclerView.layoutManager = layoutManager
        (recyclerView.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false

        recyclerView.adapter = adapter
        calendarBinding.viewModel = calendarViewModel

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        observeModel(calendarViewModel, recyclerView)
    }

    private fun observeModel(calendarViewModel: CalendarViewModel, recyclerView: CalendarFixedSizeRecyclerView) {
        disposable?.run {
            recyclerView.removeOnScrollListener(scrollListener)
            recyclerView.addOnScrollListener(scrollListener)

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
                    if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                        val scrollToIndex = (0 until adapter.itemCount).firstOrNull { adapter.getItem(it).containsDate(selectedDay) }
                        scrollToIndex?.run { layoutManager.scrollToPosition(this) }
                    }
                }
                .addTo(this)

            calendarViewModel.observeDataModeChange()
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    recyclerView.updateCurrentMode(it.first)

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

        if (null != recyclerView) {
            calendarBinding.viewModel?.run { observeModel(this, recyclerView!!) }
        }
    }

    override fun onDetachedFromWindow() {
        recyclerView?.removeOnScrollListener(scrollListener)
        disposable?.dispose()
        disposable = null

        super.onDetachedFromWindow()
    }
}

private fun UICalendarGroupDays.containsDate(selectedDay: LocalDate): Boolean =
    days.firstOrNull { it.enabled && it.date == selectedDay } != null