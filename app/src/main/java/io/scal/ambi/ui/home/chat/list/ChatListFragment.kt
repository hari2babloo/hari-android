package io.scal.ambi.ui.home.chat.list

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.R
import io.scal.ambi.databinding.FragmentChatListBinding
import io.scal.ambi.entity.chat.SmallChatItem
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.view.listenForEndScroll
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.global.base.fragment.BaseNavigationFragment
import io.scal.ambi.ui.global.view.behavior.StaticViewViewBehavior
import io.scal.ambi.ui.home.chat.details.ChatDetailsActivity
import io.scal.ambi.ui.home.chat.list.adapter.ChatListAdapter
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.android.SupportAppNavigator
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
    }

    private fun observeStates() {
        viewModel.progressState
            .toObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                adapter.showPageProgress(false)

                when (it) {
                    is ChatListProgressState.EmptyProgress   -> binding.srl.isRefreshing = true
                    is ChatListProgressState.PageProgress    -> adapter.showPageProgress(true)
                    is ChatListProgressState.RefreshProgress -> binding.srl.isRefreshing = true
                    is ChatListProgressState.NoProgress      -> {
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
                    is ChatListErrorState.NoErrorState       -> snackBar = null
                    is ChatListErrorState.FatalErrorState    -> {
                        snackBar = Snackbar.make(binding.srl, it.error.message.orEmpty(), Snackbar.LENGTH_INDEFINITE)
                        snackBar!!.setAction(R.string.text_retry, { viewModel.refresh() })
                        snackBar!!.show()
                    }
                    is ChatListErrorState.NonFatalErrorState ->
                        Toast.makeText(activity, it.error.message, Toast.LENGTH_SHORT).show()
                }
            }
            .addTo(destroyViewDisposables)

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
        get() = object : SupportAppNavigator(activity, R.id.container) {
            override fun createActivityIntent(screenKey: String?, data: Any?): Intent? =
                when (screenKey) {
                    NavigateTo.CHAT_DETAILS -> ChatDetailsActivity.createScreen(activity!!, data as SmallChatItem)
                    else                    -> null
                }

            override fun createFragment(screenKey: String?, data: Any?): Fragment? = null
        }
}