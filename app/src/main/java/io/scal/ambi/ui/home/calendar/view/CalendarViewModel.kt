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
import timber.log.Timber
import java.util.concurrent.TimeUnit

class CalendarViewModel(private val rxSchedulersAbs: RxSchedulersAbs) : ViewModel() {

    private val generationOffsetWeek = 3
    private val generationOffsetMonth = 3

    private val disposable = CompositeDisposable()

    private val weekStartDay = ObservableField<Int>(DateTimeConstants.SUNDAY)
    private val mode = ObservableField<CalendarMode>(CalendarMode.WEEK)
    private val selectedDay = ObservableField<LocalDate>()

    private val uiSelectedDay = BehaviorSubject.create<LocalDate>()
    private val weekListOfData = OptimizedObservableArrayList<UICalendarGroupDays>()
    private val monthListOfData = OptimizedObservableArrayList<UICalendarGroupDays>()

    init {
        setupCurrentDay()

        observeDateChange()
    }

    internal fun observeSelectedDay(): Observable<LocalDate> = uiSelectedDay.distinctUntilChanged()

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
        setupDay(LocalDate.now())
    }

    fun setupDay(date: LocalDate) {
        Timber.i("setup day: $date")
        selectedDay.set(date)
    }

    fun switchShowingMode() {
        val currentMode = mode.get()
        mode.set(currentMode.nextMode())
    }

    private fun observeDateChange() {
        val modeObservable = mode.toObservable().distinctUntilChanged().observeOn(rxSchedulersAbs.computationScheduler)
        val selectedDayObservable = selectedDay.toObservable().distinctUntilChanged().observeOn(rxSchedulersAbs.computationScheduler)
        val weekStartDayObservable = weekStartDay.toObservable().distinctUntilChanged().observeOn(rxSchedulersAbs.computationScheduler)

        Observable.combineLatest(modeObservable,
                                 selectedDayObservable,
                                 weekStartDayObservable,
                                 Function3<CalendarMode, LocalDate, Int, Triple<CalendarMode, LocalDate, Int>> { t1, t2, t3 -> Triple(t1, t2, t3) }
        )
            .observeOn(rxSchedulersAbs.computationScheduler)
            .toFlowable(BackpressureStrategy.LATEST)
            .throttleLast(100, TimeUnit.MILLISECONDS)
            .concatMap { triple ->
                val otherMode = if (CalendarMode.WEEK == triple.first) CalendarMode.MONTH else CalendarMode.WEEK

                val firstFlowable = buildDataForDay(triple.first, triple.second, triple.third)
                val secondFlowable = buildDataForDay(otherMode, triple.second, triple.third)

                firstFlowable.map { Triple(triple.first, triple.second, it) }
                    .concatWith(secondFlowable.map { Triple(otherMode, triple.second, it) })
            }
            .observeOn(rxSchedulersAbs.mainThreadScheduler)
            .subscribe {
                when (it.first) {
                    CalendarMode.WEEK  -> weekListOfData.replaceElements(it.third)
                    CalendarMode.MONTH -> monthListOfData.replaceElements(it.third)
                }
                uiSelectedDay.onNext(it.second)
            }
            .addTo(disposable)
    }

    private fun buildDataForDay(calendarMode: CalendarMode, mainDate: LocalDate, weekStartDay: Int): Flowable<List<UICalendarGroupDays>> =
        when (calendarMode) {
            CalendarMode.WEEK  -> buildDataForDayWeek(mainDate, weekStartDay)
            CalendarMode.MONTH -> buildDateForDayMonth(mainDate, weekStartDay)
        }

    private fun buildDataForDayWeek(mainDate: LocalDate, weekStartDay: Int): Flowable<List<UICalendarGroupDays>> {
        return Flowable.fromCallable {
            val weekStartDate = mainDate.startOfWeek(weekStartDay)

            val generationStartDate = weekStartDate.minusWeeks(generationOffsetWeek)
            val generationEndDate = weekStartDate.plusWeeks(generationOffsetWeek + 1)

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

    private fun buildDateForDayMonth(mainDate: LocalDate, weekStartDay: Int): Flowable<List<UICalendarGroupDays>> {
        return Flowable.fromCallable {
            val monthStartDate = LocalDate(mainDate.year, mainDate.monthOfYear, 1)

            val generationStartDate = monthStartDate.minusMonths(generationOffsetMonth)
            val generationEndDate = monthStartDate.plusMonths(generationOffsetMonth + 1)

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

        val days = mutableListOf<UICalendarDayWithEvents>()
        (0 until 6).forEach {
            days.addAll(generateWeekDays(weekStartDate, currentMonth))
            weekStartDate = weekStartDate.plusWeeks(1)
        }

        return UICalendarGroupDays.Month(YearMonth(monthStartDate), days)
    }

    private fun generateWeekDays(weekStartDate: LocalDate, currentMonth: Int): List<UICalendarDayWithEvents> {
        return (0 until 7).map { weekStartDate.plusDays(it) }.map { UICalendarDayWithEvents(it, it.monthOfYear == currentMonth) }
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