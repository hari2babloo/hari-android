package io.scal.ambi.ui.home.notifications

import android.content.Context
import android.databinding.ObservableField
import android.util.Log
import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.extensions.binding.observable.OptimizedObservableArrayList
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import io.scal.ambi.ui.global.model.PaginatorStateViewController
import io.scal.ambi.ui.global.model.createPaginator
import javax.inject.Inject

class NotificationsViewModel @Inject internal constructor(private val context: Context, router: BetterRouter, private val interactor: INotificationsInteractor,
                                                          private val rxSchedulersAbs: RxSchedulersAbs) : BaseViewModel(router), INotificationViewModel {

    internal val progressState = ObservableField<NotificationState>(NotificationState.TotalProgress)
    internal val errorState = ObservableField<NotificationErrorState>()
    val dataState = ObservableField<NotificationDataState>()
    var notificationCategory:String? = NotificationData.Category.individual.notificationCategory;


    private val paginator = createPaginator(
            { page -> executeLoadNextPage(page) },
            object :
                    PaginatorStateViewController<NotificationData, NotificationState, NotificationErrorState>(context, progressState, errorState) {

                override fun generateProgressEmptyState() = NotificationState.EmptyProgress
                override fun generateProgressNoState() = NotificationState.NoProgress
                override fun generateProgressRefreshState() = NotificationState.RefreshProgress
                override fun generateProgressPageState() = NotificationState.PageProgress

                override fun generateErrorFatal(message: String) = NotificationErrorState.FatalErrorState(message)
                override fun generateErrorNonFatal(message: String) = NotificationErrorState.NonFatalErrorState(message)
                override fun generateErrorNo() = NotificationErrorState.NoErrorState

                override fun showEmptyView(show: Boolean) {
                    if (show) dataState.set(NotificationDataState.NotificationEmpty(dataState.get()?.profileInfo))
                }

                override fun showData(show: Boolean, data: List<NotificationData>) {
                    if (show) {
                        dataState.set(NotificationDataState.NotificationFeed(dataState.get()?.profileInfo, OptimizedObservableArrayList(data)))
                    }
                }
            },
            true, true
    )

    fun retry() {
        refresh()
    }

    private fun executeLoadNextPage(page: Int): Single<List<NotificationData>> {
        return interactor
                .loadNotifications()
                .subscribeOn(rxSchedulersAbs.ioScheduler)
                .observeOn(rxSchedulersAbs.computationScheduler)
                .flatMap {
                    Observable.fromIterable(it)
                            .filter { it -> it.category == notificationCategory }
                            .map<NotificationData> {it}
                            .toList()
                }
                .observeOn(rxSchedulersAbs.mainThreadScheduler)
    }

    fun refresh() {
        paginator.refresh()
    }

    fun loadNextPage() {
        paginator.loadNewPage()
    }

    fun init(){
        paginator.activate()
        paginator.forceRefresh()
    }

}
