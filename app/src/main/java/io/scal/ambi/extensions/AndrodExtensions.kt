package io.scal.ambi.extensions

import android.os.Build
import android.text.SpannableStringBuilder


fun SpannableStringBuilder.appendCustom(text: CharSequence, what: Any, flags: Int): SpannableStringBuilder =
    if (Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT) {
        append(text, what, flags)
    } else {
        val start = length
        append(text)
        setSpan(what, start, length, flags)
        this
    }