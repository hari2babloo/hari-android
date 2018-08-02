package io.scal.ambi.ui.home.notifications

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.ambi.work.R
import com.ambi.work.databinding.ActivityNotificationsBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.ToolbarType
import io.scal.ambi.ui.global.base.ErrorState
import io.scal.ambi.ui.global.base.ProgressState
import io.scal.ambi.ui.global.base.activity.BaseToolbarActivity
import io.scal.ambi.ui.global.base.asErrorState
import io.scal.ambi.ui.global.base.asProgressStateSrl
import kotlin.reflect.KClass

/**
 * Created by chandra on 26-07-2018.
 */

class NotificationsActivity : BaseToolbarActivity<NotificationsViewModel, ActivityNotificationsBinding>() {


    override val layoutId: Int = R.layout.activity_notifications
    override val viewModelClass: KClass<NotificationsViewModel> = NotificationsViewModel::class
    private val adapter by lazy { NotificationAdapter(viewModel) }

    private var defaultToolbarType: ToolbarType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initRecyclerView()
        initTabbarListener();
        viewModel.init()
        observeStates()
    }

    private fun initToolbar() {
        defaultToolbarType = ToolbarType(IconImage(R.drawable.ic_close),
                Runnable { router.exit() },
                ToolbarType.TitleContent(getString(R.string.title_notifications)),
                null,
                null,
                null,
                null)

        defaultToolbarType!!.makeScrolling()

        setToolbarType(defaultToolbarType)
    }

    companion object {
        internal val EXTRA_PROFILE_UID = "EXTRA_PROFILE_UID"

        fun createScreen(context: Context) =
                Intent(context, NotificationsActivity::class.java)

    }

    private fun initTabbarListener(){

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {

                if(tab.position == 0){
                    viewModel.notificationCategory = NotificationData.Category.individual.notificationCategory
                }else if(tab.position == 1){
                    viewModel.notificationCategory = NotificationData.Category.classes.notificationCategory
                }else{
                    viewModel.notificationCategory = NotificationData.Category.group.notificationCategory
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
        binding.rvChats.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvChats.adapter = adapter
        binding.rvChats.setItemViewCacheSize(30)
        binding.rvChats.isDrawingCacheEnabled = true
        binding.rvChats.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
    }

    private fun observeStates() {
        viewModel.progressState.asProgressStateSrl(binding.srl,
                { adapter.showPageProgress(it) },
                {
                    when (it) {
                        is NotificationState.TotalProgress   -> ProgressState.NoProgress
                        is NotificationState.EmptyProgress   -> ProgressState.EmptyProgress
                        is NotificationState.PageProgress    -> ProgressState.PageProgress
                        is NotificationState.RefreshProgress -> ProgressState.RefreshProgress
                        is NotificationState.NoProgress      -> ProgressState.NoProgress
                    }
                },
                destroyDisposables)

        viewModel.errorState.asErrorState(binding.srl,
                { viewModel.retry() },
                {
                    when (it) {
                        is NotificationErrorState.NoErrorState       -> ErrorState.NoError
                        is NotificationErrorState.NonFatalErrorState -> ErrorState.NonFatalError(it.error)
                        is NotificationErrorState.FatalErrorState    -> ErrorState.FatalError(it.error)
                    }
                },
                destroyDisposables)

        var expandFirstTime = true
        viewModel.dataState
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        is NotificationDataState.NotificationFeed -> {
                            it.newsFeed.reverse()
                            adapter.updateData(it.newsFeed)
                            if (expandFirstTime) {
                                binding.appBarLayout.setExpanded(true, false)
                                //binding.vFocus.requestFocus()
                                expandFirstTime = false
                            }
                        }
                        else                                    -> adapter.releaseData()

                    }
                }
                .addTo(destroyDisposables)
    }



}