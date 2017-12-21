package io.scal.ambi.ui.home.newsfeed.list

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.R
import io.scal.ambi.databinding.FragmentNewsFeedBinding
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.view.listenForEndScroll
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.global.base.activity.BaseNavigator
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

        initRecyclerView()
        observeStates()
    }

    private fun initRecyclerView() {
        binding.rvCollegeUpdates.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//        binding.root.postDelayed({
        binding.rvCollegeUpdates.adapter = adapter
//                                 }, 5000)

        binding.rvCollegeUpdates.setItemViewCacheSize(30)
        binding.rvCollegeUpdates.isDrawingCacheEnabled = true
        binding.rvCollegeUpdates.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH

        binding.rvCollegeUpdates.listenForEndScroll(1)
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { viewModel.loadNextPage() }
    }

    private fun observeStates() {
        viewModel.progressState
            .toObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                adapter.showPageProgress(false)

                when (it) {
                    is NewsFeedProgressState.EmptyProgress   -> binding.srl.isRefreshing = true
                    is NewsFeedProgressState.PageProgress    -> adapter.showPageProgress(true)
                    is NewsFeedProgressState.RefreshProgress -> binding.srl.isRefreshing = true
                    is NewsFeedProgressState.NoProgress      -> {
                        binding.srl.isRefreshing = false
                    }
                }
            }
            .addTo(destroyViewDisposables)

        var snackBar: Snackbar? = null
        viewModel.errorState
            .toObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                snackBar?.dismiss()
                when (it) {
                    is NewsFeedErrorState.NoErrorState       -> snackBar = null
                    is NewsFeedErrorState.FatalErrorState    -> {
                        snackBar = Snackbar.make(binding.srl, it.error.message.orEmpty(), Snackbar.LENGTH_INDEFINITE)
                        snackBar!!.setAction(R.string.text_retry, { viewModel.refresh() })
                        snackBar!!.show()
                    }
                    is NewsFeedErrorState.NonFatalErrorState ->
                        Toast.makeText(activity, it.error.message, Toast.LENGTH_SHORT).show()
                }
            }
            .addTo(destroyViewDisposables)

        viewModel.dataState
            .toObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                when (it) {
                    is NewsFeedDataState.Empty -> adapter.releaseData()
                    is NewsFeedDataState.Data  -> adapter.updateData(it.newsFeed)
                }
            }
            .addTo(destroyViewDisposables)
    }

    override val navigator: Navigator?
        get() = object : BaseNavigator(activity!!) {

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
}