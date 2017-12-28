package io.scal.ambi.ui.home.calendar.view

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
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

class CalendarViewModel(private val rxSchedulersAbs: RxSchedulersAbs) : ViewModel() {

    private val disposable = CompositeDisposable()

    private val weekStartDay = ObservableField<Int>(DateTimeConstants.SUNDAY)
    private val mode = ObservableField<CalendarMode>(CalendarMode.WEEK)
    private val selectedDay = ObservableField<LocalDate>()

    private val uiSelectedDay = BehaviorSubject.create<LocalDate>()
    internal val listOfData = OptimizedObservableArrayList<UICalendarGroupDays>()

    init {
        setupCurrentDay()

        observeDateChange()
    }

    fun observeSelectedDay(): Observable<LocalDate> = uiSelectedDay

    fun setupCurrentDay() {
        setupDay(LocalDate.now())
    }

    fun setupDay(date: LocalDate) {
        selectedDay.set(date)
    }

    fun switchShowingMode() {
        val currentMode = mode.get()
        mode.set(currentMode.nextMode())
    }

    private fun observeDateChange() {
        val modeObservable = mode.toObservable().observeOn(rxSchedulersAbs.computationScheduler)
        val selectedDayObservable = selectedDay.toObservable().observeOn(rxSchedulersAbs.computationScheduler)
        val weekStartDayObservable = weekStartDay.toObservable().observeOn(rxSchedulersAbs.computationScheduler)

        Observable.combineLatest(modeObservable,
                                 selectedDayObservable,
                                 weekStartDayObservable,
                                 Function3<CalendarMode, LocalDate, Int, Triple<CalendarMode, LocalDate, Int>> { t1, t2, t3 -> Triple(t1, t2, t3) }
        )
            .observeOn(rxSchedulersAbs.computationScheduler)
            .toFlowable(BackpressureStrategy.LATEST)
            .throttleLast(100, TimeUnit.MILLISECONDS)
            .concatMap { triple ->
                val generatedDataFlowable =
                    when (triple.first) {
                        CalendarMode.WEEK -> buildWeekForDay(triple.second, triple.third)
                        else              -> Flowable.error(IllegalStateException("not implemented yet"))
                    }
                generatedDataFlowable.map { Pair(triple.second, it) }
            }
            .observeOn(rxSchedulersAbs.mainThreadScheduler)
            .subscribe {
                listOfData.replaceElements(it.second)
                uiSelectedDay.onNext(it.first)
            }
            .addTo(disposable)
    }

    private fun buildWeekForDay(mainDate: LocalDate, weekStartDay: Int): Flowable<List<UICalendarGroupDays>> {
        return Flowable.fromCallable {
            val weekStartDate = findStartOfWeek(mainDate, weekStartDay)

            val generationStartDate = weekStartDate.minusWeeks(3)
            val generationEndDate = weekStartDate.plusWeeks(4)

            val days = mutableListOf<UICalendarGroupDays>()
            var currentDate = generationStartDate
            while (currentDate != generationEndDate) {
                days.addAll(generateWeekPresentation(currentDate))
                currentDate = currentDate.plusDays(7)
            }

            days
        }
    }

    private fun generateWeekPresentation(weekStartDate: LocalDate): List<UICalendarGroupDays> {
        val weeks = mutableListOf<UICalendarGroupDays>()

        val currentMonth = weekStartDate.monthOfYear
        var days = (0 until 7).map { weekStartDate.plusDays(it) }.map { UICalendarDayWithEvents(it, it.monthOfYear == currentMonth) }
        weeks.add(UICalendarGroupDays.Week(YearMonth(weekStartDate), days))

        val weekEndDate = weekStartDate.plusDays(6)
        if (weekEndDate.monthOfYear != currentMonth) {
            days = (0 until 7).map { weekStartDate.plusDays(it) }.map { UICalendarDayWithEvents(it, it.monthOfYear == weekEndDate.monthOfYear) }
            weeks.add(UICalendarGroupDays.Week(YearMonth(weekEndDate), days))
        }
        return weeks
    }

    private fun findStartOfWeek(localDate: LocalDate, weekStartDay: Int): LocalDate {
        var currentDate = localDate
        while (currentDate.dayOfWeek != weekStartDay) {
            currentDate = currentDate.minusDays(1)
        }
        return currentDate
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
