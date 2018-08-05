package io.scal.ambi.ui.more

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.ambi.work.R
import com.ambi.work.databinding.FragmentMoreBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.auth.login.LoginActivity
import io.scal.ambi.ui.global.base.activity.BaseNavigator
import io.scal.ambi.ui.global.base.fragment.BaseNavigationFragment
import io.scal.ambi.ui.global.picker.PickerViewModel
import io.scal.ambi.ui.more.adapter.MoreAdapter
import io.scal.ambi.ui.more.data.MoreData
import io.scal.ambi.ui.profile.details.ProfileDetailsDataState
import ru.terrakok.cicerone.Navigator
import kotlin.reflect.KClass

class MoreFragment : BaseNavigationFragment<MoreViewModel, FragmentMoreBinding>() {
    override val layoutId: Int = R.layout.fragment_more
    override val viewModelClass: KClass<MoreViewModel> = MoreViewModel::class

    private val adapter by lazy { MoreAdapter(viewModel) }

    private val pickerViewModel: PickerViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(PickerViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        observeStates()
    }

    private fun initRecyclerView() {
        binding.rvMoreItems.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvMoreItems.adapter = adapter
        binding.rvMoreItems.setItemViewCacheSize(30)
        binding.rvMoreItems.isDrawingCacheEnabled = true
        binding.rvMoreItems.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        adapter.updateData(getMoreItems())
    }

    private fun getMoreItems(): List<MoreData> {
        return listOf<MoreData>(MoreData(R.drawable.ic_notebooks, "notebooks", true),
                MoreData(R.drawable.ic_campus, "campus", true),
                MoreData(R.drawable.ic_settings, "setting", false),
                MoreData(R.drawable.ic_support, "support", false),
                MoreData(R.drawable.ic_faq, "faq", false),
                MoreData(R.drawable.ic_log_out, "logout", false))
    }

    override val navigator: Navigator?
        get() = object : BaseNavigator(this) {

            override fun createActivityIntent(screenKey: String, data: Any?): Intent? =
                    when (screenKey) {
                        NavigateTo.LOGOUT ->
                            LoginActivity.createScreen(context)

                        else -> super.createActivityIntent(screenKey, data)
                    }

        }

    companion object {

        fun createScreen(url: String): MoreFragment {
            val fragment = MoreFragment()
            val args = Bundle()
            args.putString("entityType", url)
            fragment.arguments = args
            return fragment
        }
    }

    private fun observeStates() {
        viewModel.dataState
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        is ProfileDetailsDataState.DataInfoOnly -> {
                            //adapter.updateData(it)
                        }
                        else -> {
                            //todo
                        }

                    }
                }
                .addTo(destroyViewDisposables)
    }

}
