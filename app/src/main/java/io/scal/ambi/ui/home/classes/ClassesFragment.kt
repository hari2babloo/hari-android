package io.scal.ambi.ui.home.classes

/**
 * Created by chandra on 02-08-2018.
 */
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.ambi.work.R
import com.ambi.work.databinding.FragmentClassesBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.view.listenForEndScroll
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.global.base.ErrorState
import io.scal.ambi.ui.global.base.ProgressState
import io.scal.ambi.ui.global.base.activity.BaseNavigator
import io.scal.ambi.ui.global.base.asErrorState
import io.scal.ambi.ui.global.base.asProgressStateSrl
import io.scal.ambi.ui.global.base.fragment.BaseNavigationFragment
import ru.terrakok.cicerone.Navigator
import kotlin.reflect.KClass


class ClassesFragment : BaseNavigationFragment<ClassesViewModel, FragmentClassesBinding>() {

    override val layoutId: Int = R.layout.fragment_classes
    override val viewModelClass: KClass<ClassesViewModel> = ClassesViewModel::class

    private val adapter by lazy { ClassesAdapter(viewModel) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        observeStates()
        initTabbarListener()
        viewModel.init()
    }

    private fun initTabbarListener(){

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {

                if(tab.position == 0){
                    viewModel.classesCategory = ClassesData.Category.CURRENT.name
                }else if(tab.position == 1){
                    viewModel.classesCategory = ClassesData.Category.PAST.name
                }else{
                    viewModel.classesCategory = ClassesData.Category.FIND_CLASSES.name
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
        binding.rvChats.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvChats.adapter = adapter
        binding.rvChats.setItemViewCacheSize(30)
        binding.rvChats.isDrawingCacheEnabled = true
        binding.rvChats.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH

        binding.rvChats.listenForEndScroll(1)
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
                        is ClassesState.TotalProgress   -> ProgressState.NoProgress
                        is ClassesState.EmptyProgress   -> ProgressState.EmptyProgress
                        is ClassesState.PageProgress    -> ProgressState.PageProgress
                        is ClassesState.RefreshProgress -> ProgressState.RefreshProgress
                        is ClassesState.NoProgress      -> ProgressState.NoProgress
                    }
                },
                destroyViewDisposables)

        viewModel.errorState.asErrorState(binding.srl,
                { viewModel.refresh() },
                {
                    when (it) {
                        is ClassesErrorState.NoErrorState       -> ErrorState.NoError
                        is ClassesErrorState.NonFatalErrorState -> ErrorState.NonFatalError(it.error)
                        is ClassesErrorState.FatalErrorState    -> ErrorState.FatalError(it.error)
                    }
                },
                destroyViewDisposables)

        var expandFirstTime = true
        viewModel.dataState
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        is ClassesDataState.ClassesFeed -> {
                            adapter.updateData(it.newsFeed)
                        }
                        is ClassesDataState.ClassesEmpty -> adapter.releaseData()
                    }
                }
                .addTo(destroyViewDisposables)
    }

     override val navigator: Navigator?
         get() = object : BaseNavigator(this) {

             override fun createActivityIntent(screenKey: String, data: Any?): Intent? =
                     when (screenKey) {
                         NavigateTo.CLASSES_DETAILS     -> ClassesDetailsActivity.createScreen(activity!!,data as ClassesData)
                         else                           -> super.createActivityIntent(screenKey, data)
                     }

         }

    companion object {

        fun createScreen(url: String): ClassesFragment {
            val fragment = ClassesFragment()
            val args = Bundle()
            args.putString("entityType", url)
            fragment.arguments = args
            return fragment
        }
    }
}