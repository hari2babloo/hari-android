package io.scal.ambi.ui.global.model

import io.reactivex.Single

interface Paginator<in T> {

    interface ViewController<in T> {
        fun showEmptyProgress(show: Boolean)
        fun showEmptyError(show: Boolean, error: Throwable? = null)
        fun showEmptyView(show: Boolean)
        fun showData(show: Boolean, data: List<T> = emptyList())
        fun showErrorMessage(error: Throwable)
        fun showRefreshProgress(show: Boolean)
        fun showPageProgress(show: Boolean)
        fun showNoMoreData(show: Boolean, data: List<T>) {}
    }

    fun activate()

    fun refresh()

    fun forceRefresh(resetState: Boolean = true)

    fun loadNewPage()

    fun release()
}

fun <T> createPaginator(requestFactory: (Int) -> Single<List<T>>,
                        viewController: Paginator.ViewController<T>,
                        inactive: Boolean = false,
                        finishIfPageExtits: Boolean = false): Paginator<T> =
    createAppendablePaginator(requestFactory, viewController, inactive, finishIfPageExtits)

fun <T> createAppendablePaginator(requestFactory: (Int) -> Single<List<T>>,
                                  viewController: Paginator.ViewController<T>,
                                  inactive: Boolean = false,
                                  finishIfPageExtits: Boolean = false) =
    AppendablePaginator(requestFactory, viewController, inactive, finishIfPageExtits)