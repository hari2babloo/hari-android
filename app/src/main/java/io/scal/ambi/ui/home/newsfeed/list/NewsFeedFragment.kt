package io.scal.ambi.ui.home.newsfeed.list

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
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
import io.scal.ambi.ui.global.base.fragment.BaseNavigationFragment
import io.scal.ambi.ui.home.newsfeed.audience.AudienceSelectionActivity
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.android.SupportAppNavigator
import java.util.concurrent.TimeUnit
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
        binding.rvCollegeUpdates.adapter = adapter

        binding.rvCollegeUpdates.listenForEndScroll(1)
            .subscribeOn(AndroidSchedulers.mainThread())
            .skip(1, TimeUnit.SECONDS)
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
            .addTo(destroyFragmentDisposables)

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
            .addTo(destroyFragmentDisposables)

        viewModel.dataState
            .toObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                when (it) {
                    is NewsFeedDataState.Empty -> adapter.releaseData()
                    is NewsFeedDataState.Data  -> adapter.updateData(it.newsFeed)
                }
            }
            .addTo(destroyFragmentDisposables)
    }

    override val navigator: Navigator?
        get() = object : SupportAppNavigator(activity, R.id.container) {
            override fun createActivityIntent(screenKey: String?, data: Any?): Intent? =
                when (screenKey) {
                    NavigateTo.CHANGE_AUDIENCE -> AudienceSelectionActivity.createScreen(activity!!, data as? Audience)
                    else                       -> null
                }

            override fun createFragment(screenKey: String?, data: Any?): Fragment? {
                return null
            }
        }
}