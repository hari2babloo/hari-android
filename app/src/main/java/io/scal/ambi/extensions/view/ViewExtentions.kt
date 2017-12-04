package io.scal.ambi.extensions.view

import android.view.View
import android.view.ViewGroup

fun View.enableCascade(enable: Boolean) {
    if (this is ViewGroup) {
        (0 until childCount)
            .map { getChildAt(it) }
            .forEach { it.enableCascade(enable) }
    }
    this.isEnabled = enable
}