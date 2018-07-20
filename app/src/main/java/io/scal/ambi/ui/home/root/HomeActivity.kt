package io.scal.ambi.ui.home.root

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.ambi.work.R
import com.ambi.work.databinding.ActivityHomeBinding
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.ToolbarType
import io.scal.ambi.ui.auth.profile.AuthProfileCheckerViewModel
import io.scal.ambi.ui.global.base.BottomBarFragmentSwitcher
import io.scal.ambi.ui.global.base.activity.BaseNavigator
import io.scal.ambi.ui.global.base.activity.BaseToolbarActivity
import io.scal.ambi.ui.global.search.SearchToolbarContent
import io.scal.ambi.ui.home.chat.list.ChatListFragment
import io.scal.ambi.ui.home.newsfeed.list.NewsFeedFragment
import io.scal.ambi.ui.webview.ResourceWebViewFragment
import io.scal.ambi.ui.webview.SchedulerWebViewFragment
import ru.terrakok.cicerone.Navigator
import javax.inject.Inject
import kotlin.reflect.KClass

class HomeActivity : BaseToolbarActivity<HomeViewModel, ActivityHomeBinding>() {

    override val layoutId: Int = R.layout.activity_home
    override val viewModelClass: KClass<HomeViewModel> = HomeViewModel::class

    @Inject
    internal lateinit var searchViewModel: HomeSearchViewModel
    private val authProfileCheckerViewModel: AuthProfileCheckerViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(AuthProfileCheckerViewModel::class.java)
    }

    private var defaultToolbarType: ToolbarType? = null
    private lateinit var bottomBarFragmentSwitcher: BottomBarFragmentSwitcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initToolbar()
        initBottombar()
    }

    private fun initToolbar() {

        defaultToolbarType = ToolbarType(IconImage(R.drawable.ic_tab_notification_icon),
                Runnable { viewModel.openProfile() },
                SearchToolbarContent(searchViewModel),
                IconImage(R.drawable.ic_tab_chat_icon),
                Runnable { viewModel.openProfile() })

        defaultToolbarType!!.makeScrolling()

        setToolbarType(defaultToolbarType)

    }



    private fun initBottombar() {
        bottomBarFragmentSwitcher = BottomBarFragmentSwitcher(
                supportFragmentManager,
                binding.bottomBar!!.bottomBar,
                hashMapOf(
                        Pair(R.id.tab_newsfeed, NewsFeedFragment::class),
                        Pair(R.id.tab_calendar, SchedulerWebViewFragment::class),
                        Pair(R.id.tab_chat, ChatListFragment::class),
                        Pair(R.id.tab_resources, ResourceWebViewFragment::class)
                ))
        bottomBarFragmentSwitcher.showTab(R.id.tab_newsfeed)
    }

    override fun onBackPressed() {
        if (!bottomBarFragmentSwitcher.onBackPressed()) {
            super.onBackPressed()
        }
    }

    override val navigator: Navigator by lazy {
        object : BaseNavigator(this) {

        }
    }

    companion object {

        fun createScreen(context: Context) =
            Intent(context, HomeActivity::class.java)
    }
}

