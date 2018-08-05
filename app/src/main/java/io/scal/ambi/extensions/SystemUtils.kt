package io.scal.ambi.extensions

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

/**
 * Created by chandra on 04-08-2018.
 */
object SystemUtils {

    @JvmStatic
    fun toMMMddyyyy(date: DateTime) : String {
        return date.toString(DateTimeFormat.forPattern("MMM dd, yyyy"))
    }

    @JvmStatic
    fun hmmaa(HH: Int, MM: Int) : String {
        val nowDateTime = DateTime.now()
        return ((nowDateTime.hourOfDay().setCopy(HH)).minuteOfHour().setCopy(MM)).toString(DateTimeFormat.forPattern("hh:mm a"))
    }

    @JvmStatic
    fun getDayNameByDayNo(dayNo: Int) : String {
        var day = (when(dayNo){
            1 -> "Mon"
            2 -> "Tue"
            3 -> "Wed"
            4 -> "Thu"
            5 -> "Fri"
            6 -> "Sat"
            else -> "Sun"
        }
                )
        return day;
    }
}