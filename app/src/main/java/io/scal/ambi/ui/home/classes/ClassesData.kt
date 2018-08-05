package io.scal.ambi.ui.home.classes

import io.scal.ambi.extensions.SystemUtils
import org.joda.time.DateTime
import java.io.Serializable


/**
 * Created by chandra on 02-08-2018.
 */
data class ClassesData(val title: String?,
                       val courseCode: String?,
                       val professorNames: String?,
                       val endDate: DateTime,
                       val term: String?,
                       val numberOfCredits: String?,
                       val description: String?,
                       val startDate: DateTime,
                       val admins: List<String>?, val meetingDayAndTimes: String?): Serializable{

    var courseCodeProferssorNames = courseCode + " - " + professorNames
    var startend = SystemUtils.toMMMddyyyy(startDate) + " - " + SystemUtils.toMMMddyyyy(endDate)
    var adminIds = getUserIds()

    private fun getUserIds(): Map<String,String>{
        val fields = HashMap<String,String>()
        for (i in 0 until admins!!.size) {
            val user = admins!!.get(i)
            fields.put("userIds[$i]", user)
        }
        return fields
    }

    enum class Category{
        CURRENT,
        PAST,
        FIND_CLASSES
    }

}