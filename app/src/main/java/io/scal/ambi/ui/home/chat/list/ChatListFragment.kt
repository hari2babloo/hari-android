package io.scal.ambi.ui.home.chat.list

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.R
import io.scal.ambi.databinding.FragmentChatListBinding
import io.scal.ambi.entity.chat.PreviewChatItem
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.view.listenForEndScroll
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.global.base.ErrorState
import io.scal.ambi.ui.global.base.ProgressState
import io.scal.ambi.ui.global.base.activity.BaseNavigator
import io.scal.ambi.ui.global.base.asErrorState
import io.scal.ambi.ui.global.base.asProgressStateSrl
import io.scal.ambi.ui.global.base.fragment.BaseNavigationFragment
import io.scal.ambi.ui.global.view.behavior.StaticViewViewBehavior
import io.scal.ambi.ui.home.chat.details.ChatDetailsActivity
import io.scal.ambi.ui.home.chat.list.adapter.ChatListAdapter
import io.scal.ambi.ui.home.chat.newmessage.ChatNewMessageActivity
import ru.terrakok.cicerone.Navigator
import kotlin.reflect.KClass

class ChatListFragment : BaseNavigationFragment<ChatListViewModel, FragmentChatListBinding>() {

    override val layoutId: Int = R.layout.fragment_chat_list
    override val viewModelClass: KClass<ChatListViewModel> = ChatListViewModel::class

    private val adapter by lazy { ChatListAdapter(viewModel) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        observeStates()
        updateBottomFilterPosition()
    }

    private fun initRecyclerView() {
        binding.rvChats.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvChats.adapter = adapter

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
                                                           is ChatListProgressState.EmptyProgress   -> ProgressState.EmptyProgress
                                                           is ChatListProgressState.PageProgress    -> ProgressState.PageProgress
                                                           is ChatListProgressState.RefreshProgress -> ProgressState.RefreshProgress
                                                           is ChatListProgressState.NoProgress      -> ProgressState.NoProgress
                                                       }
                                                   },
                                                   destroyViewDisposables)

        viewModel.errorState.asErrorState(binding.rootContainer,
                                          { viewModel.refresh() },
                                          {
                                              when (it) {
                                                  is ChatListErrorState.NoErrorState       -> ErrorState.NoError
                                                  is ChatListErrorState.NonFatalErrorState -> ErrorState.NonFatalError(it.error)
                                                  is ChatListErrorState.FatalErrorState    -> ErrorState.FatalError(it.error)
                                              }
                                          },
                                          destroyViewDisposables)

        viewModel.filteredDataState
            .toObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                when (it) {
                    is ChatListDataState.Empty -> adapter.releaseData()
                    is ChatListDataState.Data  -> adapter.updateData(it.chats)
                }
            }
            .addTo(destroyViewDisposables)
    }

    private fun updateBottomFilterPosition() {
        if (null == view || null == activity) {
            return
        }

        val cFiltersView = binding.cFilters
        val height = cFiltersView.measuredHeight
        if (0 == height) {
            cFiltersView.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                override fun onLayoutChange(v: View, l: Int, t: Int, r: Int, b: Int, oldL: Int, oldT: Int, oldR: Int, oldB: Int) {
                    updateBottomFilterPosition()
                    cFiltersView.removeOnLayoutChangeListener(this)
                }
            })
        } else {
            val behavior = (cFiltersView.layoutParams as? CoordinatorLayout.LayoutParams)?.behavior as? StaticViewViewBehavior
            behavior?.onDependenciesViewChanged(binding.coordinator, cFiltersView, listOf(findAppBarLayout(activity!!.window.decorView)!!))
        }
    }

    private fun findAppBarLayout(root: View?): AppBarLayout? =
        when (root) {
            null            -> throw IllegalStateException("can not find app bar layout")
            is AppBarLayout -> root
            is ViewGroup    -> (0 until root.childCount).mapNotNull { findAppBarLayout(root.getChildAt(it)) }.firstOrNull()
            else            -> null
        }

    override val navigator: Navigator?
        get() = object : BaseNavigator(activity!!) {
            override fun createActivityIntent(screenKey: String, data: Any?): Intent? =
                when (screenKey) {
                    NavigateTo.CHAT_DETAILS     -> ChatDetailsActivity.createScreen(activity!!, data as PreviewChatItem)
                    NavigateTo.CHAT_NEW_MESSAGE -> ChatNewMessageActivity.createScreen(activity!!)
                    else                        -> super.createActivityIntent(screenKey, data)
                }
        }
}