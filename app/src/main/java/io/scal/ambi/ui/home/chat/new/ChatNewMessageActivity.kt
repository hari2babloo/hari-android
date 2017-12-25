package io.scal.ambi.ui.home.chat.new

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.R
import io.scal.ambi.databinding.ActivityChatNewMessageBinding
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.ToolbarType
import io.scal.ambi.ui.auth.profile.AuthProfileCheckerViewModel
import io.scal.ambi.ui.global.base.ErrorState
import io.scal.ambi.ui.global.base.ProgressState
import io.scal.ambi.ui.global.base.activity.BaseToolbarActivity
import io.scal.ambi.ui.global.base.asErrorState
import io.scal.ambi.ui.global.base.asProgressStateSrl
import io.scal.ambi.ui.home.chat.details.ChatDetailsDataState
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
        viewModel.progressState.asProgressStateSrl(binding.srl,
                                                   { adapter.showPageProgress(it) },
                                                   {
                                                       when (it) {
                                                           is ChatNewMessageProgressState.EmptyProgress   -> ProgressState.EmptyProgress
                                                           is ChatNewMessageProgressState.PageProgress    -> ProgressState.PageProgress
                                                           is ChatNewMessageProgressState.RefreshProgress -> ProgressState.RefreshProgress
                                                           is ChatNewMessageProgressState.NoProgress      -> ProgressState.NoProgress
                                                       }
                                                   },
                                                   destroyDisposables)

        viewModel.errorState.asErrorState(binding.rootContainer,
                                          { viewModel.refresh() },
                                          {
                                              when (it) {
                                                  is ChatNewMessageErrorState.NoErrorState       -> ErrorState.NoError
                                                  is ChatNewMessageErrorState.NonFatalErrorState -> ErrorState.NonFatalError(it.error)
                                                  is ChatNewMessageErrorState.FatalErrorState    -> ErrorState.FatalError(it.error)
                                              }
                                          },
                                          destroyDisposables)

        viewModel.dataState
            .toObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.chatInfo = it.chatInfo

                when (it) {
                    is ChatDetailsDataState.Data -> {
                        binding.cCreation?.root?.visibility = View.VISIBLE
                        adapter.updateData(it.allMessages)
                    }
                    else                         -> binding.cCreation?.root?.visibility = View.GONE
                }
            }
            .addTo(destroyDisposables)
    }
}