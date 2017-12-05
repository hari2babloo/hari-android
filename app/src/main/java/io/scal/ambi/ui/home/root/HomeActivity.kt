package io.scal.ambi.ui.home.root

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import io.scal.ambi.R
import io.scal.ambi.databinding.ActivityHomeBinding
import io.scal.ambi.ui.global.base.BaseToolbarActivity
import io.scal.ambi.ui.global.base.LocalNavigationHolder
import io.scal.ambi.ui.global.search.SearchToolbarContent
import io.scal.ambi.ui.global.view.ToolbarType
import io.scal.ambi.ui.home.newsfeed.NewsFeedFragment
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.android.SupportAppNavigator
import javax.inject.Inject
import kotlin.reflect.KClass

class HomeActivity : BaseToolbarActivity<HomeViewModel, ActivityHomeBinding>(), LocalNavigationHolder {

    override val layoutId: Int = R.layout.activity_home
    override val viewModelClass: KClass<HomeViewModel> = HomeViewModel::class

    @Inject
    internal lateinit var searchViewModel: HomeSearchViewModel
    override val toolbarTypeInitial
        by lazy { ToolbarType(R.drawable.ic_ambi_logo_small, SearchToolbarContent(searchViewModel), R.drawable.ic_profile) }

    private val bottomTabs: Map<Int, KClass<out Fragment>> =
        hashMapOf(
            Pair(R.id.tab_newsfeed, NewsFeedFragment::class),
            Pair(R.id.tab_calendar, Fragment::class),
            Pair(R.id.tab_chat, Fragment::class),
            Pair(R.id.tab_notifications, Fragment::class),
            Pair(R.id.tab_more, Fragment::class)
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.bottomBar.bottomBar.setOnTabSelectListener { tabId -> showTab(tabId) }
    }

    override fun getNavigationHolder(tag: String): NavigatorHolder =
        searchViewModel.getNavigationHolder(tag)

    private fun showTab(tabId: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        bottomTabs.keys
            .filter { it != tabId }
            .forEach { getTabFragment(it)?.run { transaction.detach(this) } }
        var activeTab = getTabFragment(tabId)
        if (null == activeTab) {
            activeTab = bottomTabs[tabId]!!.java.newInstance()!!
            transaction.add(R.id.container, activeTab, getTabFragmentTag(tabId))
        } else {
            transaction.attach(activeTab)
        }
        transaction.commitAllowingStateLoss()
    }

    private fun getTabFragmentTag(tabId: Int): String =
        "tab_$tabId"

    private fun getTabFragment(tabId: Int): Fragment? =
        supportFragmentManager.findFragmentByTag(getTabFragmentTag(tabId))

    override val navigator: Navigator by lazy {

        object : SupportAppNavigator(this, R.id.container) {

            override fun createActivityIntent(screenKey: String, data: Any?): Intent? {
                return null
            }

            override fun createFragment(screenKey: String, data: Any?): Fragment? {
                return null
            }
        }
    }

    companion object {

        fun createScreen(context: Context) =
            Intent(context, HomeActivity::class.java)
    }
}