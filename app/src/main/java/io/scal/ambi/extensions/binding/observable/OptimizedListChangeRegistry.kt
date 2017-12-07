package io.scal.ambi.extensions.binding.observable

import android.databinding.ListChangeRegistry
import android.databinding.ObservableList
import java.util.*

internal class OptimizedListChangeRegistry : ListChangeRegistry() {

    private var mListeners: MutableList<ObservableList.OnListChangedCallback<out ObservableList<*>>> = ArrayList()

    override fun add(callback: ObservableList.OnListChangedCallback<out ObservableList<*>>) {
        synchronized(this) {
            super.add(callback)
            mListeners.add(callback)
        }
    }

    override fun remove(callback: ObservableList.OnListChangedCallback<out ObservableList<*>>) {
        synchronized(this) {
            mListeners.remove(callback)
            super.remove(callback)
        }
    }

    fun getListeners(): List<ObservableList.OnListChangedCallback<out ObservableList<*>>> = Collections.unmodifiableList(mListeners)
}