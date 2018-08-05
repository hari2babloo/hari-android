package io.scal.ambi.ui.home.classes

import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.extensions.SystemUtils
import io.scal.ambi.extensions.notNullOrThrow
import io.scal.ambi.model.data.server.responses.Parceble
import org.joda.time.DateTime


internal class ItemClasses : Parceble<ClassesData?> {

    @SerializedName("_id")
    @Expose
    internal var id: String? = null

    @SerializedName("slug")
    @Expose
    internal var slug: String? = null


    @SerializedName("name")
    @Expose
    internal var name: String? = null

    @SerializedName("numberOfCredits")
    @Expose
    internal var numberOfCredits: Int? = null

    @SerializedName("section")
    @Expose
    internal var section: String? = null

    @SerializedName("courseCode")
    @Expose
    internal var courseCode: String? = null

    @SerializedName("creator")
    @Expose
    internal var creator: String? = null

    @SerializedName("school")
    @Expose
    internal var school: String? = null

    @SerializedName("startDate")
    @Expose
    internal var startDate: String? = null

    @SerializedName("endDate")
    @Expose
    internal var endDate: String? = null

    @SerializedName("meetingDayAndTimes")
    @Expose
    internal var meetingDayAndTimes: String? = null

    @SerializedName("description")
    @Expose
    internal var description: String? = null

    @SerializedName("deleted")
    @Expose
    internal var deleted: Boolean? = null

    @SerializedName("term")
    @Expose
    internal var term: String? = null

    @SerializedName("professorNames")
    @Expose
    internal var professorNames: List<String>? = null

    @SerializedName("admins")
    @Expose
    internal var admins: List<String>? = null

    @SerializedName("members")
    @Expose
    internal var members: List<String>? = null


    override fun parse(): ClassesData? {
        return parseAsClass()
    }

    private fun parseAsClass(): ClassesData {
        return ClassesData(name,
                courseCode,
                professorNames!!.joinToString(),
                endDate.toDateTime("endDate"),
                term,
                "" + numberOfCredits,
                description,
                startDate.toDateTime("startDate"),
                admins,getMeetingDayandTime(meetingDayAndTimes!!),members)
    }

    private fun String?.toDateTime(fieldName: String): DateTime {
        val notNullString = notNullOrThrow(fieldName)
        return DateTime.parse(notNullString)
    }

    class MeetingDayAndTimes {

        @SerializedName("days")
        @Expose
        internal var days: List<Int> = emptyList()

        @SerializedName("startHour")
        @Expose
        internal var startHour: Int? = null

        @SerializedName("startMinute")
        @Expose
        internal var startMinute: Int? = null

        @SerializedName("endHour")
        @Expose
        internal var endHour: Int? = null

        @SerializedName("endMinute")
        @Expose
        internal var endMinute: Int? = null


    }


    private fun getMeetingDayandTime(value: String): String {
        if (value.startsWith("{")) {
            var gson = Gson()
            var meetingDayAndTimes: MeetingDayAndTimes = gson.fromJson(value, MeetingDayAndTimes::class.java)
            var days: List<Int> = meetingDayAndTimes.days
            var dayForDisplay: String = ""
            var daysList = ArrayList<String>()
            for (arg in days) {
                daysList.add(SystemUtils.getDayNameByDayNo(arg))
            }
            if(daysList.size>2){
                daysList.set(daysList.size-1,"and " + daysList.get(daysList.size-1))
            }
            dayForDisplay = daysList.joinToString(", ");
            dayForDisplay = dayForDisplay.replace(", and"," and")
            var startTime: String = SystemUtils.hmmaa(meetingDayAndTimes.startHour!!, meetingDayAndTimes.startMinute!!)
            var endTime: String = SystemUtils.hmmaa(meetingDayAndTimes.endHour!!, meetingDayAndTimes.endMinute!!)
            return "$dayForDisplay, $startTime - $endTime"
        }else{
            return value
        }
    }

}