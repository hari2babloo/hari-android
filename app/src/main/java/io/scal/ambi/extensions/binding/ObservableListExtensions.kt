package io.scal.ambi.extensions.binding

import android.support.v7.util.DiffUtil
import io.scal.ambi.extensions.binding.observable.OptimizedObservableArrayList

object ObservableListExtensions {

    fun <T> swapElementsInList(mutableList: MutableList<T>, firstPosition: Int, secondPosition: Int) {
        if (firstPosition > secondPosition) {
            val firstModel = mutableList.removeAt(firstPosition)
            val secondModel = mutableList.removeAt(secondPosition)

            mutableList.add(secondPosition, firstModel)
            mutableList.add(firstPosition, secondModel)
        } else {
            val secondModel = mutableList.removeAt(secondPosition)
            val firstModel = mutableList.removeAt(firstPosition)

            mutableList.add(firstPosition, secondModel)
            mutableList.add(secondPosition, firstModel)
        }
    }

}

fun <T> MutableList<T>.swapElements(firstPosition: Int, secondPosition: Int, notifyObservers: Boolean) {
    if (this is OptimizedObservableArrayList<*>) {
        (this as OptimizedObservableArrayList<T>).swapElements(firstPosition, secondPosition, notifyObservers)
    } else {
        ObservableListExtensions.swapElementsInList(this, firstPosition, secondPosition)
    }
}

fun <T> MutableList<T>.replaceElements(newData: List<T>) =
    this.replaceElements(newData, DefaultDiffCallback(this, newData))

fun <T> MutableList<T>.replaceElements(newData: List<T>, diffCallback: DiffUtil.Callback, detectMoves: Boolean = true) {
    if (this is OptimizedObservableArrayList<*>) {
        (this as OptimizedObservableArrayList<T>).replaceElements(newData, diffCallback, detectMoves)
    } else {
        clear()
        addAll(newData)
    }
}

fun <T> MutableList<T>.replaceElement(oldItem: T, newItem: T) {
    val oldItemIndex = indexOf(oldItem)
    if (-1 != oldItemIndex) {
        if (this is OptimizedObservableArrayList<*>) {
            (this as OptimizedObservableArrayList<T>).updateElement(oldItemIndex, newItem)
        } else {
            removeAt(oldItemIndex)
            add(oldItemIndex, newItem)
        }
    }
}

open class DefaultDiffCallback<T>(private val oldList: List<T>,
                                  private val newData: List<T>,
                                  private val detectByRef: Boolean = false) : DiffUtil.Callback() {

    override fun getOldListSize(): Int =
        oldList.size

    override fun getNewListSize(): Int =
        newData.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newData[newItemPosition]
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newData[newItemPosition]
        return if (detectByRef) oldItem === newItem else oldItem == newItem
    }
}