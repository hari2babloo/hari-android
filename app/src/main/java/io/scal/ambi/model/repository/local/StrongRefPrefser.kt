package io.scal.ambi.model.repository.local

import android.content.Context
import android.content.SharedPreferences
import com.github.pwittchen.prefser.library.rx2.JsonConverter
import com.github.pwittchen.prefser.library.rx2.Prefser
import io.reactivex.Observable
import java.util.*

internal class StrongRefPrefser : Prefser {

    private val observersListeners = ArrayList<SharedPreferences.OnSharedPreferenceChangeListener>()

    constructor(context: Context) : super(context)
    constructor(context: Context, jsonConverter: JsonConverter) : super(context, jsonConverter)
    constructor(sharedPreferences: SharedPreferences) : super(sharedPreferences)
    constructor(sharedPreferences: SharedPreferences, jsonConverter: JsonConverter) : super(sharedPreferences, jsonConverter)

    override fun observePreferences(): Observable<String> {
        return Observable.create { e ->
            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                if (!e.isDisposed) {
                    e.onNext(key)
                }
            }
            preferences.registerOnSharedPreferenceChangeListener(listener)
            observersListeners.add(listener)
            e.setCancellable {
                observersListeners.remove(listener)
                preferences.unregisterOnSharedPreferenceChangeListener(listener)
            }
        }
    }
}