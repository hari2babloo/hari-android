package io.scal.ambi.ui.home.calendar.view

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.databinding.ObservableList
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function3
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject
import io.scal.ambi.extensions.binding.observable.OptimizedObservableArrayList
import io.scal.ambi.extensions.binding.replaceElements
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate
import org.joda.time.YearMonth
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class CalendarViewModel(private val rxSchedulersAbs: RxSchedulersAbs) : ViewModel() {

    private val generationOffsetWeek = 3
    private val generationOffsetMonth = 3

    private val disposable = CompositeDisposable()

    private val weekStartDay = ObservableField<Int>(DateTimeConstants.SUNDAY)
    private val mode = ObservableField<CalendarMode>(CalendarMode.WEEK)
    private val selectedDay = ObservableField<LocalDate>(LocalDate.now())

    private val uiSelectedDay = BehaviorSubject.create<LocalDate>()
    private val weekListOfData = OptimizedObservableArrayList<UICalendarGroupDays>()
    private val monthListOfData = OptimizedObservableArrayList<UICalendarGroupDays>()

    private var userAction = AtomicBoolean(false)

    init {
        initFirstData()

        observeDateChange()
    }

    fun observeVisibleDateRange(): Observable<CalendarVisibleDateRange> =
        observeDataModeChange()
            .observeOn(rxSchedulersAbs.computationScheduler)
            .map { it.second }
            .switchMap { it.toObservable() }
            .filter { it.isNotEmpty() }
            .map { CalendarVisibleDateRange(it.first().days.first().date, it.last().days.last().date) }
            .distinctUntilChanged()

    fun observeSelectedDay(): Observable<LocalDate> = uiSelectedDay.distinctUntilChanged()

    internal fun observeDataModeChange(): Observable<Pair<CalendarMode, ObservableList<UICalendarGroupDays>>> =
        mode.toObservable()
            .distinctUntilChanged()
            .switchMap {
                when (it) {
                    CalendarMode.WEEK  -> Observable.just(Pair(it, weekListOfData))
                    CalendarMode.MONTH -> Observable.just(Pair(it, monthListOfData))
                }
            }

    fun setupCurrentDay() {
        setupDayFromUserAction(LocalDate.now())
    }

    fun setupDay(date: LocalDate) {
        selectedDay.set(date)
    }

    fun setupDayFromUserAction(date: LocalDate) {
        userAction.set(true)
        setupDay(date)
    }

    fun switchShowingMode() {
        val currentMode = mode.get()
        mode.set(currentMode.nextMode())
    }

    private fun initFirstData() {
        val currentMode = mode.get()!!
        val dataList =
            when (currentMode) {
                CalendarMode.MONTH -> monthListOfData
                CalendarMode.WEEK  -> weekListOfData
            }
        if (dataList.isEmpty()) {
            val currentSelectedDay = selectedDay.get()
            buildDataForCalendar(currentMode, currentSelectedDay, weekStartDay.get(), 0)
                .subscribeOn(rxSchedulersAbs.immediateScheduler)
                .observeOn(rxSchedulersAbs.immediateScheduler)
                .firstOrError()
                .subscribe({ t ->
                               dataList.replaceElements(t)
                               uiSelectedDay.onNext(currentSelectedDay)
                           })
        }
    }

    private fun observeDateChange() {
        val selectedDayObservable = selectedDay.toObservable().distinctUntilChanged().observeOn(rxSchedulersAbs.computationScheduler)
        val modeObservable = mode.toObservable().distinctUntilChanged().observeOn(rxSchedulersAbs.computationScheduler)
        val weekStartDayObservable = weekStartDay.toObservable().distinctUntilChanged().observeOn(rxSchedulersAbs.computationScheduler)

        Observable.combineLatest(modeObservable,
                                 selectedDayObservable,
                                 weekStartDayObservable,
                                 Function3<CalendarMode, LocalDate, Int, GenerationData> { t1, t2, t3 -> GenerationData(t1, t2, t3) }
        )
            .observeOn(rxSchedulersAbs.computationScheduler)
            .toFlowable(BackpressureStrategy.LATEST)
            .throttleLast(100, TimeUnit.MILLISECONDS)
            .distinctUntilChanged { old, new ->
                if (old.weekStartDay != new.weekStartDay) {
                    // items are different
                    return@distinctUntilChanged false
                }
                if (old.selectedDay == new.selectedDay) {
                    // mode is different but selected day is the same. so we have all the data to show
                    checkUserMode()
                    return@distinctUntilChanged true
                }
                val oldStartDay = startDayByMode(old.mode, old.selectedDay, new.weekStartDay)
                val newStartDay = startDayByMode(new.mode, new.selectedDay, new.weekStartDay)
                if (oldStartDay == newStartDay) {
                    // we have same start day, so we can simply update the UI now with existing data
                    uiSelectedDay.onNext(new.selectedDay)
                }
                false
            }
            .concatMap { generationData ->
                val otherMode = if (CalendarMode.WEEK == generationData.mode) CalendarMode.MONTH else CalendarMode.WEEK

                val firstFlowable = buildDataForCalendar(generationData.mode, generationData.selectedDay, generationData.weekStartDay)
                val secondFlowable = buildDataForCalendar(otherMode, generationData.selectedDay, generationData.weekStartDay)

                firstFlowable.map { Triple(generationData.mode, generationData.selectedDay, it) }
                    .concatWith(secondFlowable.map { Triple(otherMode, generationData.selectedDay, it) })
            }
            .observeOn(rxSchedulersAbs.mainThreadScheduler)
            .subscribe {
                when (it.first) {
                    CalendarMode.WEEK  -> weekListOfData.replaceElements(it.third)
                    CalendarMode.MONTH -> monthListOfData.replaceElements(it.third)
                }
                uiSelectedDay.onNext(it.second)
                checkUserMode()
            }
            .addTo(disposable)
    }

    private fun checkUserMode() {
        if (userAction.compareAndSet(true, false)) {
            mode.set(CalendarMode.WEEK)
        }
    }

    private fun startDayByMode(mode: CalendarMode, selectedDay: LocalDate, weekStartDay: Int): LocalDate {
        return when (mode) {
            CalendarMode.WEEK  -> selectedDay.startOfWeek(weekStartDay)
            CalendarMode.MONTH -> selectedDay.startOfMonth()
        }
    }

    private fun buildDataForCalendar(calendarMode: CalendarMode,
                                     mainDate: LocalDate,
                                     weekStartDay: Int,
                                     offsetWeek: Int? = null): Flowable<List<UICalendarGroupDays>> =
        when (calendarMode) {
            CalendarMode.WEEK  -> buildDataForDayWeek(mainDate, weekStartDay, offsetWeek ?: generationOffsetWeek)
            CalendarMode.MONTH -> buildDateForDayMonth(mainDate, weekStartDay, offsetWeek ?: generationOffsetMonth)
        }

    private fun buildDataForDayWeek(mainDate: LocalDate, weekStartDay: Int, offsetWeek: Int): Flowable<List<UICalendarGroupDays>> {
        return Flowable.fromCallable {
            val weekStartDate = mainDate.startOfWeek(weekStartDay)

            val generationStartDate = weekStartDate.minusWeeks(offsetWeek)
            val generationEndDate = weekStartDate.plusWeeks(offsetWeek + 1)

            val groups = mutableListOf<UICalendarGroupDays>()
            var currentDate = generationStartDate
            while (currentDate != generationEndDate) {
                groups.addAll(generateWeekPresentation(currentDate))
                currentDate = currentDate.plusWeeks(1)
            }

            groups
        }
    }

    private fun generateWeekPresentation(weekStartDate: LocalDate): List<UICalendarGroupDays> {
        val weeks = mutableListOf<UICalendarGroupDays>()

        val currentMonth = weekStartDate.monthOfYear
        var days = generateWeekDays(weekStartDate, currentMonth)
        weeks.add(UICalendarGroupDays.Week(YearMonth(weekStartDate), days))

        val weekEndDate = weekStartDate.plusDays(6)
        if (weekEndDate.monthOfYear != currentMonth) {
            days = generateWeekDays(weekStartDate, weekEndDate.monthOfYear)
            weeks.add(UICalendarGroupDays.Week(YearMonth(weekEndDate), days))
        }
        return weeks
    }

    private fun buildDateForDayMonth(mainDate: LocalDate, weekStartDay: Int, offsetMonth: Int): Flowable<List<UICalendarGroupDays>> {
        return Flowable.fromCallable {
            val monthStartDate = mainDate.startOfMonth()

            val generationStartDate = monthStartDate.minusMonths(offsetMonth)
            val generationEndDate = monthStartDate.plusMonths(offsetMonth + 1)

            val groups = mutableListOf<UICalendarGroupDays>()
            var currentDate = generationStartDate
            while (currentDate != generationEndDate) {
                groups.add(generateMonthPresentation(currentDate, weekStartDay))
                currentDate = currentDate.plusMonths(1)
            }

            groups
        }
    }

    private fun generateMonthPresentation(monthStartDate: LocalDate, weekStartDay: Int): UICalendarGroupDays {
        val currentMonth = monthStartDate.monthOfYear
        var weekStartDate = monthStartDate.startOfWeek(weekStartDay)

        val days = mutableListOf<UICalendarDay>()
        (0 until 6).forEach {
            days.addAll(generateWeekDays(weekStartDate, currentMonth))
            weekStartDate = weekStartDate.plusWeeks(1)
        }

        return UICalendarGroupDays.Month(YearMonth(monthStartDate), days)
    }

    private fun generateWeekDays(weekStartDate: LocalDate, currentMonth: Int): List<UICalendarDay> {
        return (0 until 7).map { weekStartDate.plusDays(it) }.map { UICalendarDay(it, it.monthOfYear == currentMonth) }
    }

    fun onBackPressed(): Boolean {
        return false
    }

    public override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }
}

private fun CalendarMode.nextMode(): CalendarMode =
    when (this) {
        CalendarMode.WEEK  -> CalendarMode.MONTH
        CalendarMode.MONTH -> CalendarMode.WEEK
    }

private fun LocalDate.startOfWeek(weekStartDay: Int): LocalDate {
    var currentDate = this
    while (currentDate.dayOfWeek != weekStartDay) {
        currentDate = currentDate.minusDays(1)
    }
    return currentDate
}

private fun LocalDate.startOfMonth(): LocalDate =
    LocalDate(year, monthOfYear, 1)

private class GenerationData(val mode: CalendarMode, val selectedDay: LocalDate, val weekStartDay: Int)