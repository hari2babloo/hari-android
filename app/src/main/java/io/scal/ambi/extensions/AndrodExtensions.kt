package io.scal.ambi.extensions

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.text.SpannableStringBuilder
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel


fun SpannableStringBuilder.appendCustom(text: CharSequence, what: Any, flags: Int): SpannableStringBuilder =
    if (Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT) {
        append(text, what, flags)
    } else {
        val start = length
        append(text)
        setSpan(what, start, length, flags)
        this
    }

@Throws(IOException::class)
fun FileInputStream.copyStreamToFile(dst: File) {
    try {
        var outChannel: FileChannel? = null
        try {
            //noinspection resource,IOResourceOpenedButNotSafelyClosed
            outChannel = FileOutputStream(dst).channel

            val fullSize = channel.size()
            var transferred: Long = 0
            do {
                transferred += channel.transferTo(transferred, fullSize, outChannel)
            } while (transferred < fullSize)
            outChannel!!.force(true)
        } finally {
            try {
                outChannel?.close()
            } catch (ignored: IOException) {
                // pass because we don't care
            }
        }
    } finally {
        try {
            channel.close()
        } catch (ignored: IOException) {
            // pass because we don't care
        }
        try {
            close()
        } catch (ignored: IOException) {
            // pass because we don't care
        }
    }
}

fun Context.asActivity(): Activity =
    when {
        this is Activity       -> this
        this is ContextWrapper -> this.baseContext.asActivity()
        else                   -> throw IllegalArgumentException("context with class ${javaClass.name} can not be cast to Activity")
    }

fun Context.asAppCompatActivity(): AppCompatActivity {
    val activity = asActivity()
    if (activity is AppCompatActivity) {
        return activity
    }
    throw IllegalArgumentException("activity with class ${javaClass.name} can not be cast to AppCompatActivity")
}