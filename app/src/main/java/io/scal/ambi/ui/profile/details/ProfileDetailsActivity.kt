package io.scal.ambi.ui.profile.details

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.ambi.work.R
import com.ambi.work.databinding.ActivityProfileDetailsBinding
import com.azoft.injectorlib.InjectSavedState
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.view.SimpleDraweeView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.IconImageUser
import io.scal.ambi.extensions.view.ToolbarType
import io.scal.ambi.extensions.view.listenForEndScroll
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.auth.profile.AuthProfileCheckerViewModel
import io.scal.ambi.ui.global.base.ErrorState
import io.scal.ambi.ui.global.base.ProgressState
import io.scal.ambi.ui.global.base.activity.BaseNavigator
import io.scal.ambi.ui.global.base.activity.BaseToolbarActivity
import io.scal.ambi.ui.global.base.asErrorState
import io.scal.ambi.ui.global.base.asProgressStateSrl
import io.scal.ambi.ui.global.picker.FileResource
import io.scal.ambi.ui.global.picker.PickerViewController
import io.scal.ambi.ui.global.picker.PickerViewModel
import io.scal.ambi.ui.global.search.SearchToolbarContent
import io.scal.ambi.ui.home.newsfeed.list.adapter.NewsFeedAdapter
import io.scal.ambi.ui.profile.resume.ProfileResumeActivity
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import ru.terrakok.cicerone.Navigator
import kotlin.reflect.KClass

@RuntimePermissions
class ProfileDetailsActivity : BaseToolbarActivity<ProfileDetailsViewModel, ActivityProfileDetailsBinding>(), PickerViewController {

    override val layoutId: Int = R.layout.activity_profile_details
    override val viewModelClass: KClass<ProfileDetailsViewModel> = ProfileDetailsViewModel::class

    private lateinit var defaultToolbar: ToolbarType
    private val adapter by lazy { NewsFeedAdapter(viewModel) }

    private val pickerViewModel: PickerViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(PickerViewModel::class.java)
    }

    @InjectSavedState
    internal var attachAvatar = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.attachListener = object : AttachListener {
            override fun attachAvatarIcon() {
                attachAvatar = true
                pickerViewModel.pickAnImage(this@ProfileDetailsActivity, this@ProfileDetailsActivity)
            }

            override fun attachBannerIcon() {
                attachAvatar = false
                pickerViewModel.pickAnImage(this@ProfileDetailsActivity, this@ProfileDetailsActivity)
            }
        }
        initToolbar()
        initRecyclerView()
        observeStates()

        ViewModelProviders.of(this, viewModelFactory).get(AuthProfileCheckerViewModel::class.java)
    }

    private fun initToolbar() {
        defaultToolbar = ToolbarType(IconImage(R.drawable.ic_back), Runnable { viewModel.onBackPressed() },
                                     SearchToolbarContent(viewModel.searchViewModel),
                                     IconImageUser(),
                                     Runnable { viewModel.openProfile() })
        defaultToolbar.makePin()
        defaultToolbar.rightIconCustomization = object : ToolbarType.IconCustomization {
            override fun applyNewStyle(simpleDraweeView: SimpleDraweeView) {
                simpleDraweeView.hierarchy.apply {
                    actualImageScaleType = ScalingUtils.ScaleType.CENTER_CROP
                    roundingParams = RoundingParams.asCircle()
                }
            }
        }
        setToolbarType(defaultToolbar)
    }

    private fun initRecyclerView() {
        binding.rvFeed.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvFeed.adapter = adapter

        binding.rvFeed.setItemViewCacheSize(30)
        binding.rvFeed.isDrawingCacheEnabled = true
        binding.rvFeed.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH

        binding.rvFeed.listenForEndScroll(1)
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { viewModel.loadNextPage() }
            .addTo(destroyDisposables)
    }

    private fun observeStates() {
        viewModel.progressState.asProgressStateSrl(binding.srl,
                                                   { adapter.showPageProgress(it) },
                                                   {
                                                       binding.progress.visibility = if (it is ProfileDetailsProgressState.TotalProgress) View.VISIBLE else View.GONE

                                                       when (it) {
                                                           is ProfileDetailsProgressState.TotalProgress   -> ProgressState.NoProgress
                                                           is ProfileDetailsProgressState.EmptyProgress   -> ProgressState.EmptyProgress
                                                           is ProfileDetailsProgressState.PageProgress    -> ProgressState.PageProgress
                                                           is ProfileDetailsProgressState.RefreshProgress -> ProgressState.RefreshProgress
                                                           is ProfileDetailsProgressState.NoProgress      -> ProgressState.NoProgress
                                                       }
                                                   },
                                                   destroyDisposables)

        viewModel.errorState.asErrorState(binding.srl,
                                          { viewModel.retry() },
                                          {
                                              when (it) {
                                                  is ProfileDetailsErrorState.NoErrorState       -> ErrorState.NoError
                                                  is ProfileDetailsErrorState.NonFatalErrorState -> ErrorState.NonFatalError(it.error)
                                                  is ProfileDetailsErrorState.FatalErrorState    -> ErrorState.FatalError(it.error)
                                              }
                                          },
                                          destroyDisposables)

        var expandFirstTime = true
        viewModel.dataState
            .toObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                when (it) {
                    is ProfileDetailsDataState.DataNewsFeed -> {
                        adapter.updateData(it.newsFeed)
                        if (expandFirstTime) {
                            binding.appBarLayout.setExpanded(true, false)
                            binding.vFocus.requestFocus()
                            expandFirstTime = false
                        }
                    }
                    else                                    -> adapter.releaseData()

                }
            }
            .addTo(destroyDisposables)

        viewModel
            .currentUser
            .toObservable()
            .subscribe {
                val newToolbarType = defaultToolbar.copy(rightIcon = it.avatar)
                compareAndSetToolbarType(defaultToolbar, newToolbarType)
                defaultToolbar = newToolbarType
            }
            .addTo(destroyDisposables)
    }

    override fun setPickedFile(fileResource: FileResource, image: Boolean) {
        if (image) {
            if (attachAvatar) {
                viewModel.attachAvatar(fileResource)
            } else {
                viewModel.attachBanner(fileResource)
            }
        }
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

    override val navigator: Navigator
        get() = object : BaseNavigator(this) {
            override fun createActivityIntent(screenKey: String, data: Any?): Intent? =
                when (screenKey) {
                    NavigateTo.PROFILE_PASSWORD_CHANGE -> null
                    NavigateTo.PROFILE_RESUME          -> ProfileResumeActivity.createScreen(this@ProfileDetailsActivity)
                    NavigateTo.PROFILE_DETAILS         ->
                        if ((viewModel.profileUidToShowIsCurrent && data == null) || (viewModel.profileUid == data))
                            null
                        else
                            super.createActivityIntent(screenKey, data)
                    else                               -> super.createActivityIntent(screenKey, data)
                }
        }

    companion object {
        internal val EXTRA_PROFILE_UID = "EXTRA_PROFILE_UID"

        fun createScreen(context: Context) =
            Intent(context, ProfileDetailsActivity::class.java)

        fun createScreen(context: Context, profileUid: String) =
            Intent(context, ProfileDetailsActivity::class.java)
                .putExtra(EXTRA_PROFILE_UID, profileUid)
    }
}

interface AttachListener {

    fun attachAvatarIcon()

    fun attachBannerIcon()
}