package io.scal.ambi.extensions.view

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

fun RecyclerView.listenForEndScroll(visibleThreshold: Int): Observable<Any> {
    val subject = PublishSubject.create<Any>()
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val tmpLayoutManager = layoutManager
            if (dy > 0 && tmpLayoutManager is LinearLayoutManager) {
                val visibleItemCount = tmpLayoutManager.childCount
                val totalItemCount = tmpLayoutManager.itemCount
                val firstVisibleItem = tmpLayoutManager.findFirstVisibleItemPosition()

                if (totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
                    subject.onNext(Any())
                }
            }
        }
    })
    return subject
}