package io.scal.ambi.ui.home.chat.newmessage

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.R
import io.scal.ambi.databinding.ActivityChatNewMessageBinding
import io.scal.ambi.entity.chat.PreviewChatItem
import io.scal.ambi.extensions.binding.replaceElements
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.ToolbarType
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.auth.profile.AuthProfileCheckerViewModel
import io.scal.ambi.ui.global.base.ErrorState
import io.scal.ambi.ui.global.base.activity.BaseNavigator
import io.scal.ambi.ui.global.base.activity.BaseToolbarActivity
import io.scal.ambi.ui.global.base.asErrorState
import io.scal.ambi.ui.home.chat.details.ChatDetailsActivity
import ru.terrakok.cicerone.Navigator
import kotlin.reflect.KClass

class ChatNewMessageActivity : BaseToolbarActivity<ChatNewMessageViewModel, ActivityChatNewMessageBinding>() {

    override val layoutId: Int = R.layout.activity_chat_new_message
    override val viewModelClass: KClass<ChatNewMessageViewModel> = ChatNewMessageViewModel::class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initToolbar()
        observeStates()

        ViewModelProviders.of(this, viewModelFactory).get(AuthProfileCheckerViewModel::class.java)
    }

    private fun initToolbar() {
        setToolbarType(ToolbarType(IconImage(R.drawable.ic_back),
                                   Runnable { router.exit() },
                                   ToolbarType.TitleContent(getString(R.string.title_chat_new_message)),
                                   null,
                                   null))
    }

    private fun observeStates() {
        viewModel.progressState
            .toObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                when (it) {
                    is ChatNewMessageProgressState.NoProgress    -> {
                        if (binding.progress.visibility == View.VISIBLE) {
                            binding.progress.visibility = View.GONE
                            binding.chipsInput.showPopup()
                        }
                    }
                    is ChatNewMessageProgressState.EmptyProgress -> {
                        binding.progress.visibility = View.VISIBLE
                        binding.bAction.isEnabled = false
                        binding.chipsInput.hidePopup()
                    }
                }
            }
            .addTo(destroyDisposables)

        viewModel.errorState
            .asErrorState(binding.rootContainer,
                          { viewModel.refresh() },
                          {
                              when (it) {
                                  is ChatNewMessageErrorState.NoErrorState       -> ErrorState.NoError
                                  is ChatNewMessageErrorState.NonFatalErrorState -> ErrorState.NonFatalError(it.error)
                                  is ChatNewMessageErrorState.FatalErrorState    -> ErrorState.FatalError(it.error)
                              }
                          },
                          destroyDisposables)

        val chipsInputDisposable = CompositeDisposable()
        destroyDisposables.add(chipsInputDisposable)

        viewModel.dataState
            .toObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.cData.visibility = View.VISIBLE
                chipsInputDisposable.clear()

                when (it) {
                    is ChatNewMessageDataState.EmptyData -> {
                        binding.chipsInput.filterableList = emptyList()
                        binding.chipsInput.selectedChipList = emptyList<UIUserChip>()
                    }
                    is ChatNewMessageDataState.Data      -> {
                        binding.chipsInput.filterableList = it.users
                        binding.chipsInput.selectedChipList = it.selectedUsers

                        binding.chipsInput
                            .observeSelectedList()
                            .flatMapSingle { Observable.fromIterable(it).map { it as UIUserChip }.toList() }
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe { userSelection ->
                                binding.bAction.isEnabled = userSelection.isNotEmpty()
                                it.selectedUsers.replaceElements(userSelection)
                            }
                            .addTo(chipsInputDisposable)
                    }
                }
            }
            .addTo(destroyDisposables)

        binding.chipsInput
            .observeSelectedText()
            .map { it.trim() }
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { userText -> viewModel.updateList(userText) }
            .addTo(destroyDisposables)
    }

    override val navigator: Navigator
        get() = object : BaseNavigator(this) {
            override fun createActivityIntent(screenKey: String, data: Any?): Intent? =
                when (screenKey) {
                    NavigateTo.CHAT_DETAILS -> ChatDetailsActivity.createScreen(this@ChatNewMessageActivity, data as PreviewChatItem)
                    else                    -> super.createActivityIntent(screenKey, data)
                }
        }

    companion object {

        fun createScreen(context: Context): Intent {
            return Intent(context, ChatNewMessageActivity::class.java)
        }
    }
}