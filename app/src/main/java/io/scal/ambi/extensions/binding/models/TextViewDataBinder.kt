package io.scal.ambi.extensions.binding.models

import android.databinding.BindingAdapter
import android.graphics.Typeface
import android.support.v4.content.res.ResourcesCompat
import android.widget.TextView
import io.scal.ambi.R
import org.joda.time.LocalDateTime
import org.joda.time.Seconds
import org.joda.time.format.DateTimeFormat
import java.util.*

object TextViewDataBinder {

    private val fontNameTypefaceCache: MutableMap<String, Typeface> = HashMap()
    private val fontNameWithResource = mapOf(
        Pair("pantraRegular", R.font.nicolas_desle_pantra_regular),
        Pair("pantraBold", R.font.nicolas_desle_pantra_bold),
        Pair("pantraLight", R.font.nicolas_desle_pantra_light),
        Pair("pantraMedium", R.font.nicolas_desle_pantra_medium)
    )

    @JvmStatic
    @BindingAdapter("customFontName")
    fun bindCustomFont(textView: TextView, fontName: String?) {
        fontName?.run {
            fontNameTypefaceCache[fontName]?.run {
                textView.typeface = this
                return
            }

            val fontResource = fontNameWithResource[this]
            fontResource?.run {
                val typeface = ResourcesCompat.getFont(textView.context, this)
                if (null != typeface) {
                    textView.typeface = typeface
                    fontNameTypefaceCache.put(fontName, typeface)
                }
            }
        }
    }

    private val FULL_DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy/MM/dd").withLocale(Locale.ENGLISH)

    @JvmStatic
    @BindingAdapter("datePassed")
    fun bindDateTimePassed(textView: TextView, dateTime: LocalDateTime?) {
        if (null == dateTime) {
            textView.text = null
        } else {
            // we can use build in solution or a library here, but I don't know restrictions now, so will create simple code for that
            val context = textView.context
            val nowDateTime = LocalDateTime.now()
            val secondsBetween = Seconds.secondsBetween(dateTime, nowDateTime).toStandardDuration()
            val resultText = when {
                secondsBetween.standardSeconds < 0  -> FULL_DATE_TIME_FORMATTER.print(dateTime)
                secondsBetween.standardSeconds < 60 -> context.getString(R.string.day_ago_just_now)
                secondsBetween.standardMinutes < 60 -> context.getString(R.string.day_ago_minutes_ago, secondsBetween.standardMinutes)
                secondsBetween.standardHours < 24   -> context.getString(R.string.day_ago_hours_ago, secondsBetween.standardHours)
                secondsBetween.standardDays < 30    -> context.getString(R.string.day_ago_days_ago, secondsBetween.standardDays)
                else                                -> FULL_DATE_TIME_FORMATTER.print(dateTime)
            }
            textView.text = resultText
        }
    }
}