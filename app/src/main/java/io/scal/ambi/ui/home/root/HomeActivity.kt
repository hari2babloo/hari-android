package io.scal.ambi.ui.home.root

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.ambi.work.R
import com.ambi.work.databinding.ActivityHomeBinding
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.view.SimpleDraweeView
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.IconImageUser
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
        initNavigation()
    }

    private fun initToolbar() {
        defaultToolbarType = ToolbarType(IconImage(R.drawable.ic_ambi_logo_small),
                                         null,
                                         SearchToolbarContent(searchViewModel),
                                         IconImageUser(),
                                         Runnable { viewModel.openProfile() })
        defaultToolbarType!!.makeScrolling()
        defaultToolbarType!!.rightIconCustomization = object : ToolbarType.IconCustomization {
            override fun applyNewStyle(simpleDraweeView: SimpleDraweeView) {
                simpleDraweeView.hierarchy.apply {
                    actualImageScaleType = ScalingUtils.ScaleType.CENTER_CROP
                    roundingParams = RoundingParams.asCircle()
                }
            }
        }
        setToolbarType(defaultToolbarType)

        authProfileCheckerViewModel
            .userProfile
            .subscribe {
                val newToolbarType = defaultToolbarType?.copy(rightIcon = it.avatar)
                compareAndSetToolbarType(defaultToolbarType, newToolbarType)
                defaultToolbarType = newToolbarType
            }
            .addTo(destroyDisposables)
    }

    private fun initNavigation() {
        bottomBarFragmentSwitcher = BottomBarFragmentSwitcher(
            supportFragmentManager,
            binding.bottomBar!!.bottomBar,
            hashMapOf(
                Pair(R.id.tab_newsfeed, NewsFeedFragment::class),
//                Pair(R.id.tab_calendar, CalendarListFragment::class),
                Pair(R.id.tab_calendar, SchedulerWebViewFragment::class),
                Pair(R.id.tab_chat, ChatListFragment::class),
//                Pair(R.id.tab_notifications, Fragment::class),
                Pair(R.id.tab_resources, ResourceWebViewFragment::class),
                Pair(R.id.tab_more, Fragment::class)
            ))
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