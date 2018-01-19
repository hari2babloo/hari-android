package io.scal.ambi.ui.global.base

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.roughike.bottombar.BottomBar
import kotlin.reflect.KClass

class BottomBarFragmentSwitcher(fragmentManager: FragmentManager,
                                bottomBar: BottomBar,
                                bottomFragmentClasses: Map<Int, KClass<out Fragment>>) :
    FragmentSwitcher(fragmentManager, bottomFragmentClasses) {

    init {
        bottomBar.setOnTabSelectListener { tabId -> showTab(tabId) }
    }
}