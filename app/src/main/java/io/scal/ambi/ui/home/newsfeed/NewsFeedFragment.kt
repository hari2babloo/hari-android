package io.scal.ambi.ui.home.newsfeed

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.R
import io.scal.ambi.databinding.FragmentNewsFeedBinding
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.ui.global.base.fragment.BaseNavigationFragment
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
    }

    private fun observeStates() {
        viewModel.progressState
            .toObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                when (it) {
                    is NewsFeedProgressState.EmptyProgress   -> binding.srl.isRefreshing = true
                    is NewsFeedProgressState.PageProgress    -> {
                    }
                    is NewsFeedProgressState.RefreshProgress -> binding.srl.isRefreshing = true
                    is NewsFeedProgressState.NoProgress      -> {
                        binding.srl.isRefreshing = false
                    }
                }
            }
            .addTo(destroyFragmentDisposables)

        viewModel.errorState
            .toObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                when (it) {
                    is NewsFeedErrorState.NoErrorState       -> {
                    }
                    is NewsFeedErrorState.FatalErrorState    -> {
                    }
                    is NewsFeedErrorState.NonFatalErrorState -> Toast.makeText(activity, it.error.message, Toast.LENGTH_SHORT).show()
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
}