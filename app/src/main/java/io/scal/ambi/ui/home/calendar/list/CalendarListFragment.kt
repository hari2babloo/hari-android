package io.scal.ambi.ui.home.calendar.list

import android.os.Bundle
import android.view.View
import io.scal.ambi.R
import io.scal.ambi.databinding.FragmentCalendarListBinding
import io.scal.ambi.ui.global.base.fragment.BaseNavigationFragment
import kotlin.reflect.KClass

class CalendarListFragment : BaseNavigationFragment<CalendarListViewModel, FragmentCalendarListBinding>() {

    override val layoutId: Int = R.layout.fragment_calendar_list
    override val viewModelClass: KClass<CalendarListViewModel> = CalendarListViewModel::class

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCalendar()
    }

    private fun setupCalendar() {
        binding.calendarView.setupViewModel(viewModel.calendarViewModel)
    }
}