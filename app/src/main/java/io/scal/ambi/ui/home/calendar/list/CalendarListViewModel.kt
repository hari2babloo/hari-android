package io.scal.ambi.ui.home.calendar.list

import android.databinding.ObservableArrayMap
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.interactor.home.calendar.ICalendarListInteractor
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.base.viewmodel.BaseUserViewModel
import io.scal.ambi.ui.home.calendar.view.CalendarViewModel
import io.scal.ambi.ui.home.calendar.view.UICalendarEvents
import org.joda.time.LocalDate
import javax.inject.Inject

class CalendarListViewModel @Inject constructor(router: BetterRouter,
                                                private val interactor: ICalendarListInteractor,
                                                rxSchedulersAbs: RxSchedulersAbs) :
    BaseUserViewModel(router, { interactor.loadCurrentUser() }, rxSchedulersAbs) {

    val calendarViewModel = CalendarViewModel(rxSchedulersAbs)

    private val eventsByDate = ObservableArrayMap<LocalDate, UICalendarEvents?>()

    init {
        calendarViewModel.observeSelectedDay()
            .observeOn(rxSchedulersAbs.ioScheduler)
            .observeOn(rxSchedulersAbs.mainThreadScheduler)
            .subscribe()
            .addTo(disposables)

        calendarViewModel.observeVisibleDateRange()
            .observeOn(rxSchedulersAbs.ioScheduler)
            .distinctUntilChanged()
            .switchMap { interactor.loadEventsForRange(it.startDate, it.endDate) }
            .observeOn(rxSchedulersAbs.computationScheduler)
            .map { Pair(it.day, it.events.map { it.color }) }
            .observeOn(rxSchedulersAbs.mainThreadScheduler)
            .subscribe { eventsByDate.put(it.first, UICalendarEvents(it.second)) }
    }

    internal fun observeEvents(): Observable<Map<LocalDate, UICalendarEvents?>> =
        eventsByDate.toObservable()
            .subscribeOn(rxSchedulersAbs.mainThreadScheduler)
            .observeOn(rxSchedulersAbs.mainThreadScheduler)
            .map { eventsByDate }

    override fun onBackPressed(): Boolean =
        if (calendarViewModel.onBackPressed()) {
            true
        } else {
            super.onBackPressed()
        }

    override fun onCleared() {
        calendarViewModel.onCleared()
        super.onCleared()
    }
}