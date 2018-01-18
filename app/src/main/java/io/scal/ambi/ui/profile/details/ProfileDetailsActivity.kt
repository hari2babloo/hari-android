package io.scal.ambi.ui.profile.details

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.ambi.work.R
import com.ambi.work.databinding.ActivityProfileDetailsBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.IconImageUser
import io.scal.ambi.extensions.view.ToolbarType
import io.scal.ambi.extensions.view.listenForEndScroll
import io.scal.ambi.ui.auth.profile.AuthProfileCheckerViewModel
import io.scal.ambi.ui.global.base.ErrorState
import io.scal.ambi.ui.global.base.ProgressState
import io.scal.ambi.ui.global.base.activity.BaseNavigator
import io.scal.ambi.ui.global.base.activity.BaseToolbarActivity
import io.scal.ambi.ui.global.base.asErrorState
import io.scal.ambi.ui.global.base.asProgressStateSrl
import io.scal.ambi.ui.global.search.SearchToolbarContent
import io.scal.ambi.ui.home.newsfeed.list.adapter.NewsFeedAdapter
import ru.terrakok.cicerone.Navigator
import kotlin.reflect.KClass

class ProfileDetailsActivity : BaseToolbarActivity<ProfileDetailsViewModel, ActivityProfileDetailsBinding>() {

    override val layoutId: Int = R.layout.activity_profile_details
    override val viewModelClass: KClass<ProfileDetailsViewModel> = ProfileDetailsViewModel::class

    private val adapter by lazy { NewsFeedAdapter(viewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initToolbar()
        initRecyclerView()
        observeStates()

        ViewModelProviders.of(this, viewModelFactory).get(AuthProfileCheckerViewModel::class.java)
    }

    private fun initToolbar() {
        val toolbar = ToolbarType(IconImage(R.drawable.ic_back), Runnable { viewModel.onBackPressed() },
                                  SearchToolbarContent(viewModel.searchViewModel),
                                  IconImageUser(),
                                  Runnable { viewModel.openProfile() })
        toolbar.makePin()
        setToolbarType(toolbar)
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
                            expandFirstTime = false
                        }
                    }
                    else                                    -> adapter.releaseData()

                }
            }
            .addTo(destroyDisposables)
    }

    override val navigator: Navigator
        get() = object : BaseNavigator(this) {}

    companion object {
        internal val EXTRA_PROFILE_UID = "EXTRA_PROFILE_UID"

        fun createScreen(context: Context) =
            Intent(context, ProfileDetailsActivity::class.java)

        fun createScreen(context: Context, profileUid: String) =
            Intent(context, ProfileDetailsActivity::class.java)
                .putExtra(EXTRA_PROFILE_UID, profileUid)
    }
}