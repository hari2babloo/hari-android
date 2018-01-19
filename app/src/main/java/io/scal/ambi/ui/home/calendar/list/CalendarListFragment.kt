package io.scal.ambi.ui.home.calendar.list

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import io.reactivex.rxkotlin.addTo
import com.ambi.work.R
import com.ambi.work.databinding.FragmentCalendarListBinding
import io.scal.ambi.ui.global.base.fragment.BaseNavigationFragment
import io.scal.ambi.ui.home.calendar.view.CalendarMainAdapter
import kotlin.reflect.KClass

class CalendarListFragment : BaseNavigationFragment<CalendarListViewModel, FragmentCalendarListBinding>() {

    override val layoutId: Int = R.layout.fragment_calendar_list
    override val viewModelClass: KClass<CalendarListViewModel> = CalendarListViewModel::class

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCalendar()
        setupViewPagerAndTabs(binding.calendarView.rootView)
    }

    private fun setupCalendar() {
        val adapter = CalendarMainAdapter(viewModel.calendarViewModel)
        binding.calendarView.setupViewModel(viewModel.calendarViewModel, binding.rvDates, adapter)

        observeEventsChanges(adapter)
    }

    private fun observeEventsChanges(adapter: CalendarMainAdapter) {
        viewModel.observeEvents()
            .subscribe { adapter.updateEvent(it) }
            .addTo(destroyViewDisposables)
    }

    private fun setupViewPagerAndTabs(rootView: View) {
        val tabLayout = rootView.findViewById<TabLayout>(R.id.event_tabs)
        val viewPager = rootView.findViewById<ViewPager>(R.id.viewpager)

        viewPager.adapter = EventsAdapter(viewPager)
        tabLayout.setupWithViewPager(viewPager)

        binding.scrollTop.isNestedScrollingEnabled = false
    }

    private class EventsAdapter(private val viewGroup: ViewGroup) : PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            return container.getChildAt(position)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

        override fun getCount(): Int = viewGroup.childCount

        override fun getPageTitle(position: Int): CharSequence =
            when (position) {
                0    -> viewGroup.context.getString(R.string.title_calendar_tab_events).toUpperCase()
                1    -> viewGroup.context.getString(R.string.title_calendar_tab_to_dos).toUpperCase()
                else -> throw IllegalArgumentException("unknown title")
            }
    }
}