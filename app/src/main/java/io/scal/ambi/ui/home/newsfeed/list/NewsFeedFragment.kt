package io.scal.ambi.ui.home.newsfeed.list

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.ambi.work.R
import com.ambi.work.databinding.FragmentNewsFeedBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.entity.feed.FeedType
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.view.listenForEndScroll
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.global.base.ErrorState
import io.scal.ambi.ui.global.base.ProgressState
import io.scal.ambi.ui.global.base.activity.BaseNavigator
import io.scal.ambi.ui.global.base.asErrorState
import io.scal.ambi.ui.global.base.asProgressStateSrl
import io.scal.ambi.ui.global.base.fragment.BaseNavigationFragment
import io.scal.ambi.ui.home.newsfeed.audience.AudienceSelectionActivity
import io.scal.ambi.ui.home.newsfeed.creation.FeedItemCreation
import io.scal.ambi.ui.home.newsfeed.creation.FeedItemCreationActivity
import io.scal.ambi.ui.home.newsfeed.list.adapter.NewsFeedAdapter
import ru.terrakok.cicerone.Navigator
import kotlin.reflect.KClass


class NewsFeedFragment : BaseNavigationFragment<NewsFeedViewModel, FragmentNewsFeedBinding>() {

    override val layoutId: Int = R.layout.fragment_news_feed
    override val viewModelClass: KClass<NewsFeedViewModel> = NewsFeedViewModel::class

    private val adapter by lazy { NewsFeedAdapter(viewModel) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.entityType = FeedType.MY_FEED.feedType

        initRecyclerView()
        observeStates()
        initTabbarListener()
    }

    private fun initTabbarListener(){

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {

                if(tab.position == 0){
                    viewModel.entityType = FeedType.MY_FEED.feedType
                }else if(tab.position == 1){
                    viewModel.entityType = FeedType.SCHOOL.feedType
                }else{
                    viewModel.entityType = FeedType.OTHERS.feedType
                }

                adapter.releaseData()
                viewModel.refresh()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }

    private fun initRecyclerView() {
        binding.rvCollegeUpdates.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvCollegeUpdates.adapter = adapter

        binding.rvCollegeUpdates.setItemViewCacheSize(30)
        binding.rvCollegeUpdates.isDrawingCacheEnabled = true
        binding.rvCollegeUpdates.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH

        binding.rvCollegeUpdates.listenForEndScroll(1)
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { viewModel.loadNextPage() }
            .addTo(destroyViewDisposables)
    }

    private fun observeStates() {
        viewModel.progressState.asProgressStateSrl(binding.srl,
                                                   { adapter.showPageProgress(it) },
                                                   {
                                                       when (it) {
                                                           is NewsFeedProgressState.EmptyProgress   -> ProgressState.EmptyProgress
                                                           is NewsFeedProgressState.PageProgress    -> ProgressState.PageProgress
                                                           is NewsFeedProgressState.RefreshProgress -> ProgressState.RefreshProgress
                                                           is NewsFeedProgressState.NoProgress      -> ProgressState.NoProgress
                                                       }
                                                   },
                                                   destroyViewDisposables)

        viewModel.errorState.asErrorState(binding.srl,
                                          { viewModel.refresh() },
                                          {
                                              when (it) {
                                                  is NewsFeedErrorState.NoErrorState       -> ErrorState.NoError
                                                  is NewsFeedErrorState.NonFatalErrorState -> ErrorState.NonFatalError(it.error)
                                                  is NewsFeedErrorState.FatalErrorState    -> ErrorState.FatalError(it.error)
                                              }
                                          },
                                          destroyViewDisposables)

        var expandFirstTime = true
        viewModel.dataState
            .toObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                when (it) {
                    is NewsFeedDataState.Empty -> adapter.releaseData()
                    is NewsFeedDataState.Data  -> {
                        adapter.updateData(it.newsFeed)
                        if (expandFirstTime) {
                            binding.vFocus.requestFocus()
                            expandFirstTime = false
                        }
                    }
                }
            }
            .addTo(destroyViewDisposables)
    }

    override val navigator: Navigator?
        get() = object : BaseNavigator(this) {

            override fun createActivityIntent(screenKey: String, data: Any?): Intent? =
                when (screenKey) {
                    NavigateTo.CHANGE_AUDIENCE     -> AudienceSelectionActivity.createScreen(activity!!, data as? Audience)
                    NavigateTo.CREATE_STATUS       -> goToCreationScreen(FeedItemCreation.STATUS)
                    NavigateTo.CREATE_ANNOUNCEMENT -> goToCreationScreen(FeedItemCreation.ANNOUNCEMENT)
                    NavigateTo.CREATE_POLL         -> goToCreationScreen(FeedItemCreation.POLL)
                    else                           -> super.createActivityIntent(screenKey, data)
                }

            private fun goToCreationScreen(feedItemCreation: FeedItemCreation): Intent =
                FeedItemCreationActivity.createScreen(activity!!, feedItemCreation)
        }

    companion object {

        fun createScreen(url: String): NewsFeedFragment {
            val fragment = NewsFeedFragment()
            val args = Bundle()
            args.putString("entityType", url)
            fragment.arguments = args
            return fragment
        }
    }
}