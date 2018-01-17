package io.scal.ambi.extensions.view

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

fun RecyclerView.listenForEndScrollInverse(visibleThreshold: Int): Observable<Any> {
    return listenForEndScroll(visibleThreshold, true)
}

fun RecyclerView.listenForEndScroll(visibleThreshold: Int): Observable<Any> {
    return listenForEndScroll(visibleThreshold, false)
}

private fun RecyclerView.listenForEndScroll(visibleThreshold: Int, inverse: Boolean = false): Observable<Any> {
    val subject = PublishSubject.create<Any>()
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val tmpLayoutManager = layoutManager
            val goodDirection = if (inverse) dy <= 0 else dy >= 0
            if (goodDirection && tmpLayoutManager is LinearLayoutManager) {
                val visibleItemCount = tmpLayoutManager.childCount
                val totalItemCount = tmpLayoutManager.itemCount
                val firstVisibleItem = tmpLayoutManager.findFirstVisibleItemPosition()

                if (totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
                    subject.onNext(Any())
                }
            }
        }
    })

    var runnable: Runnable? = null
    var wayToFinish = false
    runnable = Runnable {
        if (isAttachedToWindow) {
            val tmpLayoutManager = layoutManager
            if (tmpLayoutManager is LinearLayoutManager) {
                val firstVisibleItem = tmpLayoutManager.findFirstCompletelyVisibleItemPosition()
                val lastVisibleItem = tmpLayoutManager.findLastCompletelyVisibleItemPosition()
                val totalItemCount = tmpLayoutManager.itemCount

                if (totalItemCount - 1 == firstVisibleItem + lastVisibleItem) {
                    postDelayed(runnable!!, 1000)
                    subject.onNext(Any())
                } else {
                    if (!wayToFinish) {
                        wayToFinish = true
                        postDelayed(runnable!!, 1000)
                    }
                }
            }
        }
    }
    postDelayed(runnable, 1000)

    return subject
        .toFlowable(BackpressureStrategy.DROP)
        .throttleFirst(3, TimeUnit.SECONDS)
        .toObservable()
}