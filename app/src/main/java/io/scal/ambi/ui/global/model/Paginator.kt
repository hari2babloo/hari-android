package io.scal.ambi.ui.global.model

import io.reactivex.Single
import io.reactivex.disposables.Disposable
import timber.log.Timber

@Suppress("ClassName")
class Paginator<in T>(
    private val requestFactory: (Int) -> Single<List<T>>,
    private val viewController: ViewController<T>
) {

    interface ViewController<in T> {
        fun showEmptyProgress(show: Boolean)
        fun showEmptyError(show: Boolean, error: Throwable? = null)
        fun showEmptyView(show: Boolean)
        fun showData(show: Boolean, data: List<T> = emptyList())
        fun showErrorMessage(error: Throwable)
        fun showRefreshProgress(show: Boolean)
        fun showPageProgress(show: Boolean)
    }

    private val firstPage = 1

    private var currentState: State<T> = EMPTY()
    private var currentPage = 0
    private val currentData = mutableListOf<T>()
    private var disposable: Disposable? = null

    fun refresh() {
        currentState.refresh()
    }

    fun loadNewPage() {
        currentState.loadNewPage()
    }

    fun release() {
        currentState.release()
    }

    private fun loadPage(page: Int) {
        disposable?.dispose()
        disposable = requestFactory.invoke(page)
            .subscribe(
                { currentState.newData(it) },
                { currentState.fail(it) }
            )
    }

    private inner open class State<in T> {
        open fun refresh() {}
        open fun loadNewPage() {}
        open fun newData(data: List<T>) {}
        open fun fail(error: Throwable) {
            Timber.e(error, "paginator error on state: $currentState")
        }

        fun release() {
            currentState = RELEASED()
            disposable?.dispose()
        }
    }

    private inner class EMPTY : State<T>() {

        override fun refresh() {
            super.refresh()

            currentState = EMPTY_PROGRESS()
            viewController.showEmptyProgress(true)
            loadPage(firstPage)
        }
    }

    private inner class EMPTY_PROGRESS : State<T>() {

        override fun newData(data: List<T>) {
            super.newData(data)

            if (data.isNotEmpty()) {
                currentState = DATA()
                currentData.clear()
                currentData.addAll(data)
                currentPage = firstPage
                viewController.showEmptyProgress(false)
                viewController.showData(true, currentData)
            } else {
                currentState = EMPTY_DATA()
                viewController.showEmptyProgress(false)
                viewController.showEmptyView(true)
            }
        }

        override fun fail(error: Throwable) {
            super.fail(error)

            currentState = EMPTY_ERROR()
            viewController.showEmptyProgress(false)
            viewController.showEmptyError(true, error)
        }
    }

    private inner class EMPTY_ERROR : State<T>() {

        override fun refresh() {
            super.refresh()

            currentState = EMPTY_PROGRESS()
            viewController.showEmptyError(false)
            viewController.showEmptyProgress(true)
            loadPage(firstPage)
        }
    }

    private inner class EMPTY_DATA : State<T>() {

        override fun refresh() {
            super.refresh()

            currentState = EMPTY_PROGRESS()
            viewController.showEmptyView(false)
            viewController.showEmptyProgress(true)
            loadPage(firstPage)
        }
    }

    private inner class DATA : State<T>() {

        override fun refresh() {
            super.refresh()

            currentState = REFRESH()
            viewController.showRefreshProgress(true)
            loadPage(firstPage)
        }

        override fun loadNewPage() {
            super.loadNewPage()

            currentState = PAGE_PROGRESS()
            viewController.showPageProgress(true)
            loadPage(currentPage + 1)
        }
    }

    private inner class REFRESH : State<T>() {

        override fun newData(data: List<T>) {
            super.newData(data)

            if (data.isNotEmpty()) {
                currentState = DATA()
                currentData.clear()
                currentData.addAll(data)
                currentPage = firstPage
                viewController.showRefreshProgress(false)
                viewController.showData(true, currentData)
            } else {
                currentState = EMPTY_DATA()
                currentData.clear()
                viewController.showData(false)
                viewController.showRefreshProgress(false)
                viewController.showEmptyView(true)
            }
        }

        override fun fail(error: Throwable) {
            super.fail(error)

            currentState = DATA()
            viewController.showRefreshProgress(false)
            viewController.showErrorMessage(error)
        }
    }

    private inner class PAGE_PROGRESS : State<T>() {

        override fun newData(data: List<T>) {
            super.newData(data)

            if (data.isNotEmpty()) {
                currentState = DATA()
                currentData.addAll(data)
                currentPage++
                viewController.showPageProgress(false)
                viewController.showData(true, currentData)
            } else {
                currentState = ALL_DATA()
                viewController.showPageProgress(false)
            }
        }

        override fun refresh() {
            super.refresh()

            currentState = REFRESH()
            viewController.showPageProgress(false)
            viewController.showRefreshProgress(true)
            loadPage(firstPage)
        }

        override fun fail(error: Throwable) {
            super.fail(error)

            currentState = DATA()
            viewController.showPageProgress(false)
            viewController.showErrorMessage(error)
        }
    }

    private inner class ALL_DATA : State<T>() {

        override fun refresh() {
            super.refresh()

            currentState = REFRESH()
            viewController.showRefreshProgress(true)
            loadPage(firstPage)
        }
    }

    private inner class RELEASED : State<T>()
}