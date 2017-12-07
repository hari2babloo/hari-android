package io.scal.ambi.ui.global.base

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.roughike.bottombar.BottomBar
import io.scal.ambi.R
import io.scal.ambi.ui.global.base.fragment.BaseFragment
import kotlin.reflect.KClass

class BottomBarFragmentSwitcher(private val fragmentManager: FragmentManager,
                                bottomBar: BottomBar,
                                private val bottomFragmentClasses: HashMap<Int, KClass<out Fragment>>) {

    init {
        bottomBar.setOnTabSelectListener { tabId -> showTab(tabId) }
    }

    fun onBackPressed(): Boolean {
        val currentFragment = fragmentManager.findFragmentById(R.id.container) as? BaseFragment<*, *>
        return currentFragment?.onBackPressed() ?: false
    }

    private fun showTab(tabId: Int) {
        val transaction = fragmentManager.beginTransaction()
        bottomFragmentClasses.keys
            .filter { it != tabId }
            .forEach { getTabFragment(it)?.run { transaction.detach(this) } }
        var activeTab = getTabFragment(tabId)
        if (null == activeTab) {
            activeTab = bottomFragmentClasses[tabId]!!.java.newInstance()!!
            transaction.add(R.id.container, activeTab, getTabFragmentTag(tabId))
        } else {
            transaction.attach(activeTab)
        }
        transaction.commitAllowingStateLoss()
    }

    private fun getTabFragmentTag(tabId: Int): String =
        "tab_$tabId"

    private fun getTabFragment(tabId: Int): Fragment? =
        fragmentManager.findFragmentByTag(getTabFragmentTag(tabId))
}