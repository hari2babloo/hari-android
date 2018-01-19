package io.scal.ambi.extensions.binding.observable

import android.databinding.Observable
import android.databinding.ObservableField
import android.databinding.PropertyChangeRegistry
import io.reactivex.disposables.Disposable

class ObservableFromOtherField<T>(private val source: io.reactivex.Observable<T>) : ObservableField<T>() {

    @Transient private var callbacks: PropertyChangeRegistry? = null

    private var disposable: Disposable? = null

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        synchronized(this) {
            var needInit = false
            if (callbacks == null) {
                callbacks = PropertyChangeRegistry()
                needInit = true
            }
            callbacks!!.add(callback)
            super.addOnPropertyChangedCallback(callback)

            if (needInit) {
                disposable = source.subscribe { set(it) }
            }
        }
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        synchronized(this) {
            if (callbacks == null) {
                return
            }
            callbacks!!.remove(callback)
            super.removeOnPropertyChangedCallback(callback)

            if (callbacks!!.isEmpty) {
                disposable?.dispose()
                callbacks = null
            }
        }
    }
}