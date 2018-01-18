package io.scal.ambi.ui.home.chat.details.data

import android.content.Context
import com.ambi.work.R
import org.joda.time.Duration
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import java.util.*

class UIChatDate(val dateVal: LocalDate) {

    fun getDateText(context: Context): String {
        val duration = Duration(dateVal.toDateTimeAtCurrentTime(), LocalDate.now().toDateTimeAtCurrentTime())
        return when (duration.standardDays) {
            0L   -> context.getString(R.string.chat_details_today)
            1L   -> context.getString(R.string.chat_details_yesterday)
            else -> DATE_FORMATTER.print(dateVal)
        }
    }

    companion object {

        private val DATE_FORMATTER = DateTimeFormat.forPattern("yyyy/MM/dd").withLocale(Locale.ENGLISH)
    }
}