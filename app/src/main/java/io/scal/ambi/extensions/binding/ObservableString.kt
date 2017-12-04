package io.scal.ambi.extensions.binding

import android.databinding.ObservableField

class ObservableString(data: String? = null) {

    val data = ObservableField<String>(data)

    fun get(): String =
        data.get().orEmpty()
}