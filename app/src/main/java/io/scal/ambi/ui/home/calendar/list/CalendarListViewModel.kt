package io.scal.ambi.ui.home.calendar.list

import io.reactivex.rxkotlin.addTo
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.interactor.home.calendar.ICalendarListInteractor
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.base.viewmodel.BaseUserViewModel
import io.scal.ambi.ui.home.calendar.view.CalendarViewModel
import javax.inject.Inject

class CalendarListViewModel @Inject constructor(router: BetterRouter,
                                                private val interactor: ICalendarListInteractor,
                                                rxSchedulersAbs: RxSchedulersAbs) :
    BaseUserViewModel(router, { interactor.loadCurrentUser() }, rxSchedulersAbs) {

    val calendarViewModel = CalendarViewModel(rxSchedulersAbs)

    init {
        calendarViewModel.observeSelectedDay()
            .observeOn(rxSchedulersAbs.ioScheduler)
            .observeOn(rxSchedulersAbs.mainThreadScheduler)
            .subscribe()
            .addTo(disposables)

        calendarViewModel.observeVisibleDateRange()
            .observeOn(rxSchedulersAbs.ioScheduler)
            .switchMap { interactor.loadEventsForRange(it.startDate, it.endDate) }
            .observeOn(rxSchedulersAbs.mainThreadScheduler)
            .subscribe { calendarViewModel.updateEventsForDay() }
    }

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