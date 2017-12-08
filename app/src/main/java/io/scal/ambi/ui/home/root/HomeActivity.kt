package io.scal.ambi.ui.home.root

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.R
import io.scal.ambi.databinding.ActivityHomeBinding
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.IconImageUser
import io.scal.ambi.extensions.view.ToolbarType
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.auth.login.LoginActivity
import io.scal.ambi.ui.auth.profile.AuthProfileCheckerViewModel
import io.scal.ambi.ui.global.base.BottomBarFragmentSwitcher
import io.scal.ambi.ui.global.base.activity.BaseToolbarActivity
import io.scal.ambi.ui.global.search.SearchToolbarContent
import io.scal.ambi.ui.home.newsfeed.NewsFeedFragment
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.android.SupportAppNavigator
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
        defaultToolbarType = ToolbarType(IconImage(R.drawable.ic_ambi_logo_small), SearchToolbarContent(searchViewModel), IconImageUser())
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
            binding.bottomBar.bottomBar,
            hashMapOf(
                Pair(R.id.tab_newsfeed, NewsFeedFragment::class),
                Pair(R.id.tab_calendar, Fragment::class),
                Pair(R.id.tab_chat, Fragment::class),
                Pair(R.id.tab_notifications, Fragment::class),
                Pair(R.id.tab_more, Fragment::class)
            ))
    }

    override fun onBackPressed() {
        if (!bottomBarFragmentSwitcher.onBackPressed()) {
            super.onBackPressed()
        }
    }

    override val navigator: Navigator by lazy {

        object : SupportAppNavigator(this, R.id.container) {

            override fun createActivityIntent(screenKey: String, data: Any?): Intent? =
                when (screenKey) {
                    NavigateTo.LOGIN -> LoginActivity.createScreen(this@HomeActivity)
                    else             -> null
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