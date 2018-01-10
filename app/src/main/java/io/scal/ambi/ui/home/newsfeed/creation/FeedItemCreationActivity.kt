package io.scal.ambi.ui.home.newsfeed.creation

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.widget.TextView
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.R
import io.scal.ambi.databinding.ActivityFeedItemCreationBinding
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.IconImageUser
import io.scal.ambi.extensions.view.ToolbarType
import io.scal.ambi.ui.auth.profile.AuthProfileCheckerViewModel
import io.scal.ambi.ui.global.base.FragmentSwitcher
import io.scal.ambi.ui.global.base.activity.BaseToolbarActivity
import io.scal.ambi.ui.home.newsfeed.creation.announcement.AnnouncementCreationFragment
import io.scal.ambi.ui.home.newsfeed.creation.polls.PollsCreationFragment
import io.scal.ambi.ui.home.newsfeed.creation.status.StatusUpdateFragment
import kotlin.reflect.KClass

class FeedItemCreationActivity : BaseToolbarActivity<FeedItemCreationViewModel, ActivityFeedItemCreationBinding>() {

    override val layoutId: Int = R.layout.activity_feed_item_creation
    override val viewModelClass: KClass<FeedItemCreationViewModel> = FeedItemCreationViewModel::class

    private val authProfileCheckerViewModel: AuthProfileCheckerViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(AuthProfileCheckerViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initToolbar()
        initContent()
    }

    private fun initToolbar() {
        var defaultToolbarType = ToolbarType(IconImageUser(),
                                             null,
                                             ToolbarType.TitleContent(getString(R.string.title_creation_post)),
                                             IconImage(R.drawable.ic_close),
                                             Runnable { router.exit() })
        setToolbarType(defaultToolbarType)

        authProfileCheckerViewModel
            .userProfile
            .subscribe {
                val newToolbarType = defaultToolbarType.copy(leftIcon = it.avatar)
                compareAndSetToolbarType(defaultToolbarType, newToolbarType)
                defaultToolbarType = newToolbarType
            }
            .addTo(destroyDisposables)
    }

    private fun initContent() {
        val fragmentClasses = FeedItemCreation.values()
            .map {
                when (it) {
                    FeedItemCreation.STATUS       -> StatusUpdateFragment::class
                    FeedItemCreation.ANNOUNCEMENT -> AnnouncementCreationFragment::class
                    FeedItemCreation.POLL         -> PollsCreationFragment::class
                }
            }
            .mapIndexed { index, kClass -> Pair(index, kClass) }
            .fold(HashMap<Int, KClass<out Fragment>>() as Map<Int, KClass<out Fragment>>, { acc, pair -> acc.plus(pair) })

        val switcher = FragmentSwitcher(supportFragmentManager, fragmentClasses)

        viewModel
            .selectedFeedItem
            .toObservable()
            .distinctUntilChanged()
            .subscribe { selectedTab ->
                switcher.showTab(selectedTab.ordinal)
                (0 until binding.cTabs.childCount)
                    .map { binding.cTabs.getChildAt(it) }
                    .filter { it is TextView }
                    .map { it as TextView }
                    .forEach {
                        if (it.id == selectedTab.id) {
                            it.setTextColor(ContextCompat.getColor(this, R.color.blue))
                            it.setBackgroundResource(R.color.white)
                        } else {
                            it.setTextColor(ContextCompat.getColor(this, R.color.grayUnselectedWhite))
                            it.setBackgroundResource(R.color.whiteDark)
                        }
                    }
            }
            .addTo(destroyDisposables)
    }

    companion object {

        const val EXTRA_SELECTED_CREATION = "FeedItemCreation"

        internal fun createScreen(context: Context, feedItemCreation: FeedItemCreation): Intent {
            val intent = Intent(context, FeedItemCreationActivity::class.java)
            intent.putExtra(EXTRA_SELECTED_CREATION, feedItemCreation)
            return intent
        }
    }
}

private val FeedItemCreation.id: Int
    get() =
        when (this) {
            FeedItemCreation.STATUS       -> R.id.tv_status_update
            FeedItemCreation.ANNOUNCEMENT -> R.id.tv_announcement
            FeedItemCreation.POLL         -> R.id.tv_poll
        }
