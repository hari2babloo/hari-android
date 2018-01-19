package io.scal.ambi.ui.home.newsfeed.creation

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.widget.TextView
import com.ambi.work.R
import com.ambi.work.databinding.ActivityFeedItemCreationBinding
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.IconImageUser
import io.scal.ambi.extensions.view.ToolbarType
import io.scal.ambi.ui.auth.profile.AuthProfileCheckerViewModel
import io.scal.ambi.ui.global.base.FragmentSwitcher
import io.scal.ambi.ui.global.base.activity.BaseToolbarActivity
import io.scal.ambi.ui.global.picker.FileResource
import io.scal.ambi.ui.global.picker.PickerViewController
import io.scal.ambi.ui.global.picker.PickerViewModel
import io.scal.ambi.ui.home.newsfeed.creation.announcement.AnnouncementCreationFragment
import io.scal.ambi.ui.home.newsfeed.creation.polls.PollsCreationFragment
import io.scal.ambi.ui.home.newsfeed.creation.status.StatusUpdateFragment
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import kotlin.reflect.KClass

@RuntimePermissions
class FeedItemCreationActivity : BaseToolbarActivity<FeedItemCreationViewModel, ActivityFeedItemCreationBinding>(), PickerViewController {

    override val layoutId: Int = R.layout.activity_feed_item_creation
    override val viewModelClass: KClass<FeedItemCreationViewModel> = FeedItemCreationViewModel::class

    private lateinit var switcher: FragmentSwitcher

    private val authProfileCheckerViewModel: AuthProfileCheckerViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(AuthProfileCheckerViewModel::class.java)
    }

    internal val pickerViewModel: PickerViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(PickerViewModel::class.java)
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
        @Suppress("UNCHECKED_CAST")
        val fragmentClasses = FeedItemCreation.values()
            .map {
                when (it) {
                    FeedItemCreation.STATUS       -> StatusUpdateFragment::class
                    FeedItemCreation.ANNOUNCEMENT -> AnnouncementCreationFragment::class
                    FeedItemCreation.POLL         -> PollsCreationFragment::class
                }
                    as KClass<out Fragment>
            }
            .mapIndexed { index, kClass -> Pair(index, kClass) }
            .fold(HashMap<Int, KClass<out Fragment>>() as Map<Int, KClass<out Fragment>>, { acc, pair -> acc.plus(pair) })

        switcher = FragmentSwitcher(supportFragmentManager, fragmentClasses)

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

    override fun setPickedFile(fileResource: FileResource, image: Boolean) {
        (switcher.getCurrentTabFragment() as? ICreationFragment)?.setPickedFile(fileResource, image)
    }

    override fun showPickerDialogFragment(dialogFragment: DialogFragment) {
        val ft = supportFragmentManager.beginTransaction()
        ft.add(dialogFragment, null)
        ft.commitAllowingStateLoss()
    }

    override fun askForReadExternalStoragePermission() = notifyPickerViewModelAboutPermissionWithPermissionCheck()

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    internal fun notifyPickerViewModelAboutPermission() = pickerViewModel.onReadExternalStoragePermissionGranted(this)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        pickerViewModel.onActivityResult(this, requestCode, resultCode, data)
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        onRequestPermissionsResult(requestCode, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
