package io.scal.ambi.entity.feed

import org.joda.time.Duration

sealed class PollEndsTime {

    abstract class TimeDuration(val duration: Duration) : PollEndsTime() {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is TimeDuration) return false

            if (duration != other.duration) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + duration.hashCode()
            return result
        }
    }

    class OneDay : TimeDuration(Duration.standardDays(1))

    class OneWeek : TimeDuration(Duration.standardDays(7))

    open class Custom(duration: Duration) : TimeDuration(duration)

    class CustomDefault : Custom(Duration.standardDays(30))

    object Never : PollEndsTime()
}