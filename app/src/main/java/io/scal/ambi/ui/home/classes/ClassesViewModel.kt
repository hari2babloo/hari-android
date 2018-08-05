package io.scal.ambi.ui.home.classes

/**
 * Created by chandra on 02-08-2018.
 */

import android.content.Context
import android.databinding.ObservableField
import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.extensions.binding.observable.OptimizedObservableArrayList
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import io.scal.ambi.ui.global.model.PaginatorStateViewController
import io.scal.ambi.ui.global.model.createPaginator
import javax.inject.Inject

class ClassesViewModel @Inject internal constructor(private val context: Context, router: BetterRouter, private val interactor: IClassesInteractor,
                                                    private val rxSchedulersAbs: RxSchedulersAbs) : BaseViewModel(router), IClassesViewModel {

    override fun selectClass(element: ClassesData) {
        router.navigateTo(NavigateTo.CLASSES_DETAILS,element)
    }

    var classesCategory:String? = ClassesData.Category.CURRENT.name;
    internal val progressState = ObservableField<ClassesState>(ClassesState.TotalProgress)
    internal val errorState = ObservableField<ClassesErrorState>()
    val dataState = ObservableField<ClassesDataState>()


    private val paginator = createPaginator(
            { page -> executeLoadNextPage(page) },
            object :
                    PaginatorStateViewController<ClassesData, ClassesState, ClassesErrorState>(context, progressState, errorState) {

                override fun generateProgressEmptyState() = ClassesState.EmptyProgress
                override fun generateProgressNoState() = ClassesState.NoProgress
                override fun generateProgressRefreshState() = ClassesState.RefreshProgress
                override fun generateProgressPageState() = ClassesState.PageProgress

                override fun generateErrorFatal(message: String) = ClassesErrorState.FatalErrorState(message)
                override fun generateErrorNonFatal(message: String) = ClassesErrorState.NonFatalErrorState(message)
                override fun generateErrorNo() = ClassesErrorState.NoErrorState

                override fun showEmptyView(show: Boolean) {
                    if (show) dataState.set(ClassesDataState.ClassesEmpty(dataState.get()?.profileInfo))
                }

                override fun showData(show: Boolean, data: List<ClassesData>) {
                    if (show) {
                        dataState.set(ClassesDataState.ClassesFeed(dataState.get()?.profileInfo, OptimizedObservableArrayList(data)))
                    }
                }
            },
            true, true
    )

    fun retry() {
        refresh()
    }

    private fun executeLoadNextPage(page: Int): Single<List<ClassesData>> {
        return interactor
                .loadClasses(1)
                .subscribeOn(rxSchedulersAbs.ioScheduler)
                .observeOn(rxSchedulersAbs.computationScheduler)
                .flatMap {
                    if(classesCategory.equals(ClassesData.Category.PAST.name)){
                        Observable.fromIterable(it)
                                .filter {
                                    it -> it.endDate.isBeforeNow
                                }
                                .map<ClassesData> {it}
                                .toList()
                    }else if(classesCategory.equals(ClassesData.Category.CURRENT.name)){
                        Observable.fromIterable(it)
                                .filter {
                                    it -> it.endDate.isEqualNow or it.endDate.isAfterNow
                                }
                                .map<ClassesData> {it}
                                .toList()
                    }else {
                        Observable.fromIterable(it)
                                .map<ClassesData> {it}
                                .toList()
                    }

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
