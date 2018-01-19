package io.scal.ambi.extensions.binding.binders

import android.databinding.BindingAdapter
import android.support.v4.widget.SwipeRefreshLayout

object RecyclerViewBinder {

    @JvmStatic
    @BindingAdapter("onSwipeToRefresh")
    fun onSwipeToRefresh(swipeRefreshLayout: SwipeRefreshLayout, action: Runnable?) {
        swipeRefreshLayout.setOnRefreshListener { action?.run() }
    }
}