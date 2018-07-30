package io.scal.ambi.extensions.binding.models

import android.databinding.BindingAdapter
import android.graphics.Typeface
import android.support.v4.content.res.ResourcesCompat
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import com.ambi.work.R
import io.scal.ambi.entity.feed.PollEndsTime
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.Seconds
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.PeriodFormatterBuilder
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
    private val TIME_DATE_TIME_FORMATTER = DateTimeFormat.forPattern("HH:mma").withLocale(Locale.ENGLISH)
    private val DAY_OF_WEEK_DATE_TIME_FORMATTER = DateTimeFormat.forPattern("EEEE").withLocale(Locale.ENGLISH)
    private val DATE_DATE_TIME_FORMATTER = DateTimeFormat.forPattern("MMM dd").withLocale(Locale.ENGLISH)

    @JvmStatic
    @BindingAdapter("datePassed")
    fun bindDateTimePassed(textView: TextView, dateTime: DateTime?) {
        if (null == dateTime) {
            textView.text = null
        } else {
            // we can use build in solution or a library here, but I don't know restrictions now, so will create simple code for that
            val context = textView.context
            val nowDateTime = DateTime.now()
            val durationBetween = Seconds.secondsBetween(dateTime, nowDateTime).toStandardDuration()
            val resultText = when {
                durationBetween.standardMinutes < -20 -> FULL_DATE_TIME_FORMATTER.print(dateTime)
                durationBetween.standardSeconds < 60  -> context.getString(R.string.day_ago_just_now)
                durationBetween.standardMinutes in 2..59 -> context.getString(R.string.day_ago_minutes_ago, durationBetween.standardMinutes)
                durationBetween.standardMinutes in 1..1 -> context.getString(R.string.day_ago_minute_ago, durationBetween.standardMinutes)
                durationBetween.standardHours < 24    -> context.getString(R.string.day_ago_hours_ago, durationBetween.standardHours)
                durationBetween.standardDays < 30     -> context.getString(R.string.day_ago_days_ago, durationBetween.standardDays)
                else                                  -> FULL_DATE_TIME_FORMATTER.print(dateTime)
            }
            textView.text = resultText
        }
    }

    @JvmStatic
    @BindingAdapter("datePassedSmall")
    fun bindDateTimePassedSmall(textView: TextView, dateTime: DateTime?) {
        if (null == dateTime) {
            textView.text = null
        } else {
            // we can use build in solution or a library here, but I don't know restrictions now, so will create simple code for that
            val context = textView.context
            val nowDateTime = DateTime.now()
            val durationBetween = Seconds.secondsBetween(dateTime, nowDateTime).toStandardDuration()
            val resultText = when {
                durationBetween.standardMinutes < -20 -> DATE_DATE_TIME_FORMATTER.print(dateTime)
                durationBetween.standardSeconds < 60  -> context.getString(R.string.day_ago_just_now_small)
                durationBetween.standardMinutes < 60  -> context.getString(R.string.day_ago_minutes_ago, durationBetween.standardMinutes)
                durationBetween.standardHours < 24    -> context.getString(R.string.day_ago_hours_ago, durationBetween.standardHours)
                durationBetween.standardDays < 7      -> DAY_OF_WEEK_DATE_TIME_FORMATTER.print(dateTime)
                else                                  -> DATE_DATE_TIME_FORMATTER.print(dateTime)
            }
            textView.text = resultText
        }
    }

    @JvmStatic
    @BindingAdapter("durationPollEnds")
    fun bindDurationPollEnds(textView: TextView, pollEndsTime: PollEndsTime?) {
        if (null == pollEndsTime) {
            textView.text = null
        } else {
            val context = textView.context
            val formatter = PeriodFormatterBuilder()
                .appendDays()
                .appendSuffix("d")
                .appendHours()
                .appendSuffix("h")
                .appendMinutes()
                .appendSuffix("m")
                .toFormatter()

            val text =
                when (pollEndsTime) {
                    is PollEndsTime.OneDay       -> context.getString(R.string.creation_poll_ends_duration_1_day)
                    is PollEndsTime.OneWeek      -> context.getString(R.string.creation_poll_ends_duration_1_week)
                    is PollEndsTime.UserCustom   -> context.getString(R.string.creation_poll_ends_duration_custom_title)
                    PollEndsTime.Never           -> context.getString(R.string.creation_poll_ends_duration_never)
                    is PollEndsTime.TimeDuration -> throw IllegalStateException("todo implement custom text")
                }
            textView.text = text
        }
    }

    @JvmStatic
    @BindingAdapter("durationPollEndsIn")
    fun bindDurationPollEndsIn(textView: TextView, pollEndsTime: DateTime?) {
        if (null == pollEndsTime) {
            textView.text = null
        } else {
            val context = textView.context
            val formatter = PeriodFormatterBuilder()
                .appendDays()
                .appendSuffix("d ")
                .appendHours()
                .appendSuffix("h ")
                .appendMinutes()
                .appendSuffix("m")
                .toFormatter()

            val pollDurationLeft = Duration(DateTime.now(), pollEndsTime)
            if (pollDurationLeft.millis < 0) {
                // poll was ended
                textView.text = null
            } else {
                textView.text = context.getString(R.string.news_feed_poll_ends_in, formatter.print(pollDurationLeft.toPeriodFrom(DateTime.now())))
            }
        }
    }

    @JvmStatic
    @BindingAdapter("inputType")
    fun bindInputType(editText: EditText, inputType: Int?) {
        if (null == inputType || -1 == inputType) {
        } else {
            editText.inputType = inputType

            editText.isFocusable = true
            editText.isFocusableInTouchMode = true
            editText.isCursorVisible = true
        }
    }

    @JvmStatic
    @BindingAdapter("textResId")
    fun bindTextRes(textView: TextView, textRes: Int?) {
        if (textRes == null || textRes == 0) {
            textView.text = null
        } else {
            textView.setText(textRes)
        }
    }

    @JvmStatic
    @BindingAdapter("boldTextStyle")
    fun bindBoldTextStyle(textView: TextView, boldText: Boolean?) {
        if (null != boldText && boldText) {
            textView.setTypeface(null, Typeface.BOLD)
        } else {
            textView.setTypeface(null, Typeface.NORMAL)
        }
    }

    @JvmStatic
    @BindingAdapter("maxLines")
    fun bindMaxLines(textView: TextView, maxLines: Int?) {
        if (null == maxLines || 0 >= maxLines) {
            textView.maxLines = -1
        } else {
            textView.maxLines = maxLines
        }
    }

    @JvmStatic
    @BindingAdapter("imeDoneClickListener")
    fun bindImeDoneClickListener(editText: EditText, imeDoneClickListener: DoneActionClickListener?) {
        if (null == imeDoneClickListener) {
            editText.setOnEditorActionListener(null)
        } else {
            val listener = TextView.OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || event.action == KeyEvent.ACTION_DOWN
                    && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                    imeDoneClickListener.onDoneActionClicked()
                    true
                } else {
                    false
                }
            }
            editText.setOnEditorActionListener(listener)
        }
    }

    interface DoneActionClickListener {

        fun onDoneActionClicked()
    }
}