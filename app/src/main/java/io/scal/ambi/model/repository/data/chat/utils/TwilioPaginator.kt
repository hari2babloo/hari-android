package io.scal.ambi.model.repository.data.chat.utils

import com.twilio.chat.CallbackListener
import com.twilio.chat.Paginator
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import java.util.concurrent.Executors

internal class TwilioPaginator<T, R>(private val initialPaginatorCreator: () -> Single<Paginator<T>>,
                                     private val convertItemToResult: (T) -> Observable<R>,
                                     private val rxSchedulersAbs: RxSchedulersAbs) {

    private val executor = Schedulers.from(Executors.newSingleThreadExecutor())
    private val allChannelPaginatorInformation = BehaviorSubject.createDefault<PaginatorInformation<T>>(
        PaginatorInformation())

    fun loadPage(page: Int): Single<List<R>> {
        return allChannelPaginatorInformation
            .toFlowable(BackpressureStrategy.LATEST)
            .observeOn(executor)
            .firstOrError()
            .flatMap {
                if (it.currentPage == page - 1 && it.isValid()) {
                    Single.just(it.paginatorHolder)
                } else {
                    it.dispose()
                    Single.error(IllegalStateException("wrong paginator state, skipping"))
                }
            }
            .flatMap {
                if (it.hasNextPage()) {
                    loadNextPage(it.paginator!!)
                } else {
                    Single.just(PaginatorHolder(null))
                }
            }
            .onErrorResumeNext(createChatChannelPaginator(page))
            .doOnSuccess { allChannelPaginatorInformation.onNext(PaginatorInformation(it,
                                                                                                                                                    page)) }
            .map { it.paginator?.items ?: emptyList<T>() }
            .observeOn(rxSchedulersAbs.computationScheduler)
            .flatMap {
                Observable.fromIterable(it)
                    .concatMap { convertItemToResult.invoke(it) }
                    .toList()
            }
    }

    private fun createChatChannelPaginator(page: Int): Single<PaginatorHolder<T>> {
        return initialPaginatorCreator
            .invoke()
            .subscribeOn(executor)
            .observeOn(executor)
            .map { PaginatorHolder(it) }
            .flatMap { loopNextPageLoading(page - 1, it) }
    }

    private fun <T> loadNextPage(paginator: Paginator<T>): Single<PaginatorHolder<T>> {
        return Single.create<PaginatorHolder<T>> { e ->
            if (paginator.hasNextPage()) {
                val listener = object : CallbackListener<Paginator<T>>() {
                    override fun onSuccess(p0: Paginator<T>) {
                        if (!e.isDisposed) {
                            e.onSuccess(PaginatorHolder(p0))
                        }
                    }
                }

                paginator.requestNextPage(listener)
            } else {
                e.onSuccess(PaginatorHolder(null))
            }
        }
            .subscribeOn(executor)
            .observeOn(executor)
    }

    private fun <T> loopNextPageLoading(page: Int, paginatorHolder: PaginatorHolder<T>): Single<PaginatorHolder<T>> =
        if (page < 0 || !paginatorHolder.hasNextPage()) {
            Single.just(paginatorHolder)
        } else {
            loadNextPage(paginatorHolder.paginator!!)
                .flatMap { loopNextPageLoading(page - 1, it) }
        }

    private class PaginatorInformation<T>(val paginatorHolder: PaginatorHolder<T> = PaginatorHolder(), val currentPage: Int? = null) {

        fun isValid(): Boolean =
            try {
                if (null == paginatorHolder.paginator) {
                    false
                } else {
                    paginatorHolder.paginator.hasNextPage()
                    true
                }
            } catch (e: Exception) {
                false
            }

        fun dispose() {
            try {
                paginatorHolder.paginator?.dispose()
            } catch (e: Exception) {
            }
        }

    }

    private class PaginatorHolder<T>(val paginator: Paginator<T>? = null) {
        fun hasNextPage(): Boolean = paginator?.hasNextPage() ?: false

    }
}