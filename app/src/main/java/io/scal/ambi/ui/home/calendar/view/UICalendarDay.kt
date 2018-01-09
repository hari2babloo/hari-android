package io.scal.ambi.ui.home.calendar.view

import org.joda.time.LocalDate

data class UICalendarDay(val date: LocalDate,
                         val enabled: Boolean)