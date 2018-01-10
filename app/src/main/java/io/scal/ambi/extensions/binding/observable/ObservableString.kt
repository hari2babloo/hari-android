package io.scal.ambi.extensions.binding.observable

import android.databinding.ObservableBoolean
import android.databinding.ObservableField

class ObservableString(data: String? = null) {

    val data = ObservableField<String>(data)
    val enabled = ObservableBoolean(true)

    fun get(): String =
        data.get().orEmpty()
}