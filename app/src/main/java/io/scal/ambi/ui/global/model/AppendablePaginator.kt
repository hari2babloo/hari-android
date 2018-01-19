package io.scal.ambi.ui.global.model

import io.reactivex.Single
import io.reactivex.disposables.Disposable
import timber.log.Timber

@Suppress("ClassName")
class AppendablePaginator<in T>(
    private val requestFactory: (Int) -> Single<List<T>>,
    private val viewController: Paginator.ViewController<T>,
    inactive: Boolean = false,
    private val finishIfPageExists: Boolean = false
) : Paginator<T> {

    private val firstPage = 1

    private var currentState: State<T> = if (inactive) INACTIVE() else EMPTY()
    private var currentPage = 0
    private val currentData = mutableListOf<T>()
    private var disposable: Disposable? = null

    fun isNotActivated(): Boolean {
        return currentState is INACTIVE
    }

    override fun activate() {
        currentState.activate()
    }

    override fun refresh() {
        currentState.refresh()
    }

    override fun forceRefresh(resetState: Boolean) {
        currentState.forceRefresh(resetState)
    }

    override fun loadNewPage() {
        currentState.loadNewPage()
    }

    override fun release() {
        currentState.release()
    }

    fun appendNewData(newData: List<T>) {
        currentState.appendNewData(newData)
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
        open fun activate() {}
        open fun refresh() {}
        open fun forceRefresh(resetState: Boolean) {
            if (resetState) {
                viewController.showData(false)
                viewController.showEmptyView(false)
                viewController.showEmptyError(false)
                viewController.showPageProgress(false)
                viewController.showRefreshProgress(false)
                viewController.showEmptyProgress(false)

                currentState = EMPTY()
                currentState.refresh()
            } else {
                currentState = EMPTY_PROGRESS()
                viewController.showPageProgress(true)
                loadPage(firstPage)
            }
        }

        open fun loadNewPage() {}
        open fun newData(data: List<T>) {}
        open fun appendNewData(data: List<T>) {}
        open fun fail(error: Throwable) {
            Timber.e(error, "paginator error on state: $currentState")
        }

        fun release() {
            currentState = RELEASED()
            disposable?.dispose()
        }
    }

    private inner class INACTIVE : State<T>() {

        override fun forceRefresh(resetState: Boolean) {}

        override fun activate() {
            super.activate()

            currentState = EMPTY()
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

            viewController.showPageProgress(false)

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
                viewController.showNoMoreData(true, emptyList())
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

        override fun appendNewData(data: List<T>) {
            super.appendNewData(data)

            if (data.isNotEmpty()) {
                currentState = DATA()
                currentData.clear()
                currentData.addAll(data)
                currentPage = firstPage
                viewController.showEmptyView(false)
                viewController.showData(true, currentData)
            }
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

        override fun appendNewData(data: List<T>) {
            super.appendNewData(data)

            if (data.isNotEmpty()) {
                appendNewData(currentData, data)
                viewController.showData(true, currentData)
            }
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
                viewController.showNoMoreData(true, emptyList())
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
                if (finishIfPageExists && currentData.containsAll(data)) {
                    // all items are the same. so nothing to add
                    currentState = ALL_DATA()
                    viewController.showPageProgress(false)
                    viewController.showNoMoreData(true, currentData)
                } else {
                    currentState = DATA()
                    currentData.addAll(data)
                    currentPage++
                    viewController.showPageProgress(false)
                    viewController.showData(true, currentData)
                }
            } else {
                currentState = ALL_DATA()
                viewController.showPageProgress(false)
                viewController.showNoMoreData(true, currentData)
            }
        }

        override fun appendNewData(data: List<T>) {
            super.appendNewData(data)

            if (data.isNotEmpty()) {
                appendNewData(currentData, data)
                viewController.showData(true, currentData)
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

        override fun appendNewData(data: List<T>) {
            super.appendNewData(data)

            if (data.isNotEmpty()) {
                appendNewData(currentData, data)
                viewController.showData(true, currentData)
            }
        }
    }

    private inner class RELEASED : State<T>()

    private fun appendNewData(currentData: MutableList<T>, dataToAdd: List<T>) {
        val updatedItems = mutableListOf<T>()
        val newItems = mutableListOf<T>()
        dataToAdd.forEach { if (currentData.contains(it)) updatedItems.add(it) else newItems.add(it) }

        currentData.addAll(0, newItems)

        updatedItems.forEach {
            val index = currentData.indexOf(it)
            if (-1 != index) {
                currentData.removeAt(index)
                currentData.add(index, it)
            }
        }
    }
}