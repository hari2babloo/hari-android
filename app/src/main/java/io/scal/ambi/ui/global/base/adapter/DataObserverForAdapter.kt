package io.scal.ambi.ui.global.base.adapter

import android.databinding.ObservableList
import android.support.v7.widget.RecyclerView
import java.lang.ref.WeakReference

open class DataObserverForAdapter<Item>(private val items: ObservableList<Item>, adapter: RecyclerView.Adapter<*>) {

    private val listener = BaseOnListChangedCallback(items, adapter, this)

    init {
        items.addOnListChangedCallback(listener)
    }

    fun getItemCount(): Int = items.size

    fun getItem(position: Int): Item {
        return items[position]
    }

    fun release() {
        items.removeOnListChangedCallback(listener)
    }

    open fun notifyDataSetChanged(adapter: RecyclerView.Adapter<*>) {
        adapter.notifyDataSetChanged()
    }

    open fun notifyItemRangeChanged(adapter: RecyclerView.Adapter<*>, positionStart: Int, itemCount: Int) {
        adapter.notifyItemRangeChanged(positionStart, itemCount)
    }

    open fun notifyItemRangeInserted(adapter: RecyclerView.Adapter<*>, toPosition: Int, itemCount: Int) {
        adapter.notifyItemRangeInserted(toPosition, itemCount)
    }

    open fun notifyItemRangeRemoved(adapter: RecyclerView.Adapter<*>, fromPosition: Int, itemCount: Int) {
        adapter.notifyItemRangeRemoved(fromPosition, itemCount)
    }

    private class BaseOnListChangedCallback<Item> internal constructor(private val items: ObservableList<Item>,
                                                                       adapter: RecyclerView.Adapter<*>,
                                                                       private val adapterDataObserver: DataObserverForAdapter<Item>) :
        ObservableList.OnListChangedCallback<ObservableList<Item>>() {

        private val adapterWeakReference: WeakReference<RecyclerView.Adapter<*>> = WeakReference(adapter)

        override fun onChanged(sender: ObservableList<Item>) {
            val adapter = adapter
            adapter?.run {
                adapterDataObserver.notifyDataSetChanged(this)
            }
        }

        override fun onItemRangeChanged(sender: ObservableList<Item>, positionStart: Int, itemCount: Int) {
            val adapter = adapter
            adapter?.run {
                adapterDataObserver.notifyItemRangeChanged(this, positionStart, itemCount)
            }
        }

        override fun onItemRangeInserted(sender: ObservableList<Item>, positionStart: Int, itemCount: Int) {
            val adapter = adapter
            adapter?.run {
                adapterDataObserver.notifyItemRangeInserted(this, positionStart, itemCount)
            }
        }

        override fun onItemRangeMoved(sender: ObservableList<Item>, fromPosition: Int, toPosition: Int, itemCount: Int) {
            val adapter = adapter
            adapter?.run {
                adapterDataObserver.notifyItemRangeRemoved(this, fromPosition, itemCount)
                adapterDataObserver.notifyItemRangeInserted(this, toPosition, itemCount)
            }
        }

        override fun onItemRangeRemoved(sender: ObservableList<Item>, positionStart: Int, itemCount: Int) {
            val adapter = adapter
            adapter?.run {
                adapterDataObserver.notifyItemRangeRemoved(this, positionStart, itemCount)
            }
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