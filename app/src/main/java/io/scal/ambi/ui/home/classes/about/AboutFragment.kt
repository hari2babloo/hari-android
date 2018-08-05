package io.scal.ambi.ui.home.classes.about

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.ambi.work.R
import com.ambi.work.databinding.FragmentClassesAboutBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.ui.global.base.fragment.BaseNavigationFragment
import io.scal.ambi.ui.home.classes.ClassesData
import io.scal.ambi.ui.home.classes.members.MembersAdapter
import kotlin.reflect.KClass

/**
 * Created by chandra on 03-08-2018.
 */

class AboutFragment : BaseNavigationFragment<AboutViewModel,FragmentClassesAboutBinding>(){

    override val layoutId: Int = R.layout.fragment_classes_about
    override val viewModelClass: KClass<AboutViewModel> = AboutViewModel::class

    private val adapter by lazy { MembersAdapter(viewModel) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView();
        observeStates();
    }

    companion object {

        internal val EXTRA_CLASS_ITEM = "EXTRA_CLASS_ITEM"

        fun createScreen(classItem: ClassesData): AboutFragment {
            val fragment = AboutFragment()
            val args = Bundle()
            args.putSerializable(EXTRA_CLASS_ITEM, classItem)
            fragment.arguments = args
            return fragment
        }
    }


    private fun observeStates() {
        viewModel.stateModel
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        is AboutDataState.AboutFeed -> {
                            adapter.updateData(it.newsFeed)
                        }
                        else -> adapter.releaseData()
                    }
                }
                .addTo(destroyViewDisposables)
    }

    private fun initRecyclerView() {
        binding.rcvAdminsList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rcvAdminsList.adapter = adapter
        binding.rcvAdminsList.setItemViewCacheSize(30)
        binding.rcvAdminsList.isDrawingCacheEnabled = true
        binding.rcvAdminsList.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
    }

}