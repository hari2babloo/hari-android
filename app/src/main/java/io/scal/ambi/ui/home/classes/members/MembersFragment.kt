package io.scal.ambi.ui.home.classes.members

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.RelativeLayout
import com.ambi.work.R
import com.ambi.work.databinding.FragmentMembersBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.ui.global.base.fragment.BaseNavigationFragment
import io.scal.ambi.ui.home.classes.ClassesData
import io.scal.ambi.ui.home.classes.about.AboutDataState
import kotlin.reflect.KClass


/**
 * Created by chandra on 05-08-2018.
 */
class MembersFragment: BaseNavigationFragment<MembersViewModel,FragmentMembersBinding>(){

    override val layoutId: Int = R.layout.fragment_members
    override val viewModelClass: KClass<MembersViewModel> = MembersViewModel::class
    private lateinit var sheetBehavior: BottomSheetBehavior<RelativeLayout>

    private val adapter by lazy { MembersAdapter(viewModel) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView();
        observeStates();
        sheetBehavior = BottomSheetBehavior.from<RelativeLayout>(binding.bmsView)
    }

    private fun observeStates() {

        viewModel.openAction
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(){
                    if (sheetBehavior!!.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                        sheetBehavior!!.setState(BottomSheetBehavior.STATE_EXPANDED);
                    } else {
                        sheetBehavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }

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
        binding.rcvMembersList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rcvMembersList.adapter = adapter
        binding.rcvMembersList.setItemViewCacheSize(30)
        binding.rcvMembersList.isDrawingCacheEnabled = true
        binding.rcvMembersList.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
    }

    companion object {

        internal val EXTRA_CLASS_ITEM = "EXTRA_CLASS_ITEM"

        fun createScreen(classItem: ClassesData): MembersFragment {
            val fragment = MembersFragment()
            val args = Bundle()
            args.putSerializable(EXTRA_CLASS_ITEM, classItem)
            fragment.arguments = args
            return fragment
        }
    }
}