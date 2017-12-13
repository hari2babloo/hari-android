package io.scal.ambi.extensions.binding.observable

import android.databinding.ObservableArrayList
import android.support.v7.util.DiffUtil
import android.support.v7.util.ListUpdateCallback
import io.scal.ambi.extensions.binding.ObservableListExtensions.swapElementsInList

class OptimizedObservableArrayList<T> : ObservableArrayList<T> {

    @Transient
    private var listChangeRegistry: OptimizedListChangeRegistry? = OptimizedListChangeRegistry()

    constructor() : super()

    constructor(list: List<T>?) : super() {
        list?.run { addAll(this) }
    }

    override fun addOnListChangedCallback(listener: android.databinding.ObservableList.OnListChangedCallback<*>) {
        super.addOnListChangedCallback(listener)

        if (null == listChangeRegistry) {
            listChangeRegistry = OptimizedListChangeRegistry()
        }
        listChangeRegistry!!.add(listener)
    }

    override fun removeOnListChangedCallback(listener: android.databinding.ObservableList.OnListChangedCallback<*>) {
        if (null != listChangeRegistry) {
            listChangeRegistry!!.remove(listener)
        }

        super.removeOnListChangedCallback(listener)
    }

    fun replaceElements(list: List<T>, diffCallback: DiffUtil.Callback, detectMoves: Boolean) {
        val diffResult = DiffUtil.calculateDiff(diffCallback, detectMoves)

        executeChange(false, {
            clear()
            addAll(list)
        })

        diffResult.dispatchUpdatesTo(object : ListUpdateCallback {
            override fun onChanged(position: Int, count: Int, payload: Any?) {
                listChangeRegistry?.notifyChanged(this@OptimizedObservableArrayList, position, count)
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                listChangeRegistry?.notifyMoved(this@OptimizedObservableArrayList, fromPosition, toPosition, 1)
            }

            override fun onInserted(position: Int, count: Int) {
                listChangeRegistry?.notifyInserted(this@OptimizedObservableArrayList, position, count)
            }

            override fun onRemoved(position: Int, count: Int) {
                listChangeRegistry?.notifyRemoved(this@OptimizedObservableArrayList, position, count)
            }
        })
    }

    fun updateElement(oldItemIndex: Int, newItem: T) {
        executeChange(false, {
            removeAt(oldItemIndex)
            add(oldItemIndex, newItem)
        })
        listChangeRegistry?.notifyChanged(this, oldItemIndex, 1)
    }

    fun swapElements(firstPosition: Int, secondPosition: Int, notifyObservers: Boolean = true) {
        executeChange(notifyObservers, { swapElementsInList(this, firstPosition, secondPosition) })
    }

    private fun executeChange(notifyObservers: Boolean, changeFunction: () -> Unit) {
        val removeObservers = null != listChangeRegistry && !notifyObservers
        if (removeObservers) {
            listChangeRegistry!!.getListeners().forEach { callback -> super.removeOnListChangedCallback(callback) }
        }

        changeFunction.invoke()

        if (removeObservers) {
            listChangeRegistry!!.getListeners().forEach { callback -> super.addOnListChangedCallback(callback) }
        }
    }
}