package io.scal.ambi.ui.global.base

import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import io.scal.ambi.extensions.view.BottomBarType
import kotlin.reflect.KClass

class BottomBarFragmentSwitcher(fragmentManager: FragmentManager,
                                bottomBar: BottomBarType,
                                bottomFragmentClasses: Map<Int, KClass<out Fragment>>) :
        FragmentSwitcher(fragmentManager, bottomFragmentClasses) {

    init {

        bottomBar.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {
                showTab(Integer.valueOf(tab.tag.toString()))
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

    }
}