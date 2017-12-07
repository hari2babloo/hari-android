package io.scal.ambi.ui.global.base.adapter

import android.databinding.ObservableList
import android.support.v7.widget.RecyclerView
import java.lang.ref.WeakReference

class BaseAdapterDataObserver<Item>(private val items: ObservableList<Item>,
                                    adapter: RecyclerView.Adapter<*>) {

    private val listener = BaseOnListChangedCallback(items, adapter)

    init {
        items.addOnListChangedCallback(listener)
    }

    fun getItemCount(): Int = items.size

    fun release() {
        items.removeOnListChangedCallback(listener)
    }

    private class BaseOnListChangedCallback<Item> internal constructor(private val items: ObservableList<Item>,
                                                                       adapter: RecyclerView.Adapter<*>) :
        ObservableList.OnListChangedCallback<ObservableList<Item>>() {

        private val adapterWeakReference: WeakReference<RecyclerView.Adapter<*>> = WeakReference(adapter)

        override fun onChanged(sender: ObservableList<Item>) {
            val adapter = adapter
            adapter?.notifyDataSetChanged()
        }

        override fun onItemRangeChanged(sender: ObservableList<Item>, positionStart: Int, itemCount: Int) {
            val adapter = adapter
            adapter?.notifyItemRangeChanged(positionStart, itemCount)
        }

        override fun onItemRangeInserted(sender: ObservableList<Item>, positionStart: Int, itemCount: Int) {
            val adapter = adapter
            adapter?.notifyItemRangeInserted(positionStart, itemCount)
        }

        override fun onItemRangeMoved(sender: ObservableList<Item>, fromPosition: Int, toPosition: Int, itemCount: Int) {
            val adapter = adapter
            if (null != adapter) {
                adapter.notifyItemRangeRemoved(fromPosition, itemCount)
                adapter.notifyItemRangeInserted(toPosition, itemCount)
            }
        }

        override fun onItemRangeRemoved(sender: ObservableList<Item>, positionStart: Int, itemCount: Int) {
            val adapter = adapter
            adapter?.notifyItemRangeRemoved(positionStart, itemCount)
        }

        private val adapter: RecyclerView.Adapter<*>?
            get() {
                val adapter = adapterWeakReference.get()
                if (null == adapter) {
                    items.removeOnListChangedCallback(this)
                }
                return adapter
            }

    }
}