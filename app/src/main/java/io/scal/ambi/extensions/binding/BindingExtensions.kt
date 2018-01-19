package io.scal.ambi.extensions.binding

import android.databinding.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

fun ObservableBoolean.toObservable(): Observable<Boolean> =
    Observable.create({ e ->
                          val field = this
                          val listener = object : android.databinding.Observable.OnPropertyChangedCallback() {
                              override fun onPropertyChanged(p0: android.databinding.Observable?, p1: Int) {
                                  if (!e.isDisposed) {
                                      e.onNext(field.get())
                                  }
                              }
                          }
                          e.onNext(get())
                          addOnPropertyChangedCallback(listener)
                          e.setCancellable { removeOnPropertyChangedCallback(listener) }
                      })

fun ObservableInt.toObservable(): Observable<Int> =
    Observable.create({ e ->
                          val field = this
                          val listener = object : android.databinding.Observable.OnPropertyChangedCallback() {
                              override fun onPropertyChanged(p0: android.databinding.Observable?, p1: Int) {
                                  if (!e.isDisposed) {
                                      e.onNext(field.get())
                                  }
                              }
                          }
                          e.onNext(get())
                          addOnPropertyChangedCallback(listener)
                          e.setCancellable { removeOnPropertyChangedCallback(listener) }
                      })

fun <T> ObservableField<T>.toObservable(): Observable<T> =
    Observable.create({ e ->
                          val field = this
                          val listener = object : android.databinding.Observable.OnPropertyChangedCallback() {
                              override fun onPropertyChanged(p0: android.databinding.Observable?, p1: Int) {
                                  if (!e.isDisposed) {
                                      e.onNext(field.get())
                                  }
                              }
                          }
                          get()?.let { e.onNext(it) }
                          addOnPropertyChangedCallback(listener)
                          e.setCancellable { removeOnPropertyChangedCallback(listener) }
                      })


fun <K, V> ObservableMap<K, V?>.toObservable(): Observable<Pair<K, V?>> =
    Observable.create({ e ->
                          val propertyChangeCallback = object : ObservableMap.OnMapChangedCallback<ObservableMap<K, V?>, K, V?>() {
                              override fun onMapChanged(sender: ObservableMap<K, V?>, key: K) {
                                  if (!e.isDisposed) {
                                      e.onNext(Pair(key, sender[key]))
                                  }
                              }
                          }

                          this.forEach { e.onNext(Pair(it.key, it.value)) }

                          this.addOnMapChangedCallback(propertyChangeCallback)
                          e.setCancellable { this.removeOnMapChangedCallback(propertyChangeCallback) }
                      })

fun <T> ObservableList<T>.toObservable(): Observable<List<T>> =
    Observable.create({ e ->
                          val onListChangedCallback = object : ObservableList.OnListChangedCallback<ObservableList<T>>() {
                              override fun onChanged(sender: ObservableList<T>) {
                                  onDataChanged(sender)
                              }

                              override fun onItemRangeChanged(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
                                  onDataChanged(sender)
                              }

                              override fun onItemRangeInserted(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
                                  onDataChanged(sender)
                              }

                              override fun onItemRangeMoved(sender: ObservableList<T>, fromPosition: Int, toPosition: Int, itemCount: Int) {
                                  onDataChanged(sender)
                              }

                              override fun onItemRangeRemoved(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
                                  onDataChanged(sender)
                              }

                              private fun onDataChanged(sender: ObservableList<T>) {
                                  if (!e.isDisposed) {
                                      e.onNext(ArrayList(sender))
                                  }
                              }
                          }

                          e.onNext(ArrayList(this))

                          this.addOnListChangedCallback(onListChangedCallback)
                          e.setCancellable { this.removeOnListChangedCallback(onListChangedCallback) }
                      })

fun <T : BaseObservable> T.toObservable(): Observable<T> =
    Observable.create({ e ->
                          val field = this
                          val propertyChangeCallback = object : android.databinding.Observable.OnPropertyChangedCallback() {
                              override fun onPropertyChanged(sender: android.databinding.Observable, propertyId: Int) {
                                  if (!e.isDisposed) {
                                      e.onNext(field)
                                  }
                              }
                          }

                          e.onNext(field)
                          addOnPropertyChangedCallback(propertyChangeCallback)
                          e.setCancellable { removeOnPropertyChangedCallback(propertyChangeCallback) }
                      })

fun <T> Observable<T>.toBinding(compositeDisposable: CompositeDisposable): ObservableField<T> {
    val obsField = ObservableField<T>()
    this
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ obsField.set(it) }, {})
        .addTo(compositeDisposable)
    return obsField
}
