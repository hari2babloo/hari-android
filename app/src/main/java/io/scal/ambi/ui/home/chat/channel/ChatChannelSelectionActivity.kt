package io.scal.ambi.ui.home.chat.channel

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import com.ambi.work.R
import com.ambi.work.databinding.ActivityChatChannelSelectionBinding
import io.scal.ambi.entity.chat.ChatChannelDescription
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.ToolbarType
import io.scal.ambi.ui.auth.profile.AuthProfileCheckerViewModel
import io.scal.ambi.ui.global.base.ErrorState
import io.scal.ambi.ui.global.base.activity.BaseToolbarActivity
import io.scal.ambi.ui.global.base.asErrorState
import java.io.Serializable
import kotlin.reflect.KClass

class ChatChannelSelectionActivity : BaseToolbarActivity<ChatChannelSelectionViewModel, ActivityChatChannelSelectionBinding>() {

    override val layoutId: Int = R.layout.activity_chat_channel_selection
    override val viewModelClass: KClass<ChatChannelSelectionViewModel> = ChatChannelSelectionViewModel::class

    private lateinit var defaultToolbar: ToolbarType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initToolbar()
        initRecyclerView()
        observeStates()

        ViewModelProviders.of(this, viewModelFactory).get(AuthProfileCheckerViewModel::class.java)
    }

    private fun initToolbar() {
        defaultToolbar = ToolbarType(IconImage(R.drawable.ic_close), Runnable { router.exit() }, null, null, null)
        setToolbarType(defaultToolbar)
    }

    private fun initRecyclerView() {
        binding.rvChannels.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun observeStates() {
        viewModel.progressState
            .toObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                when (it) {
                    ChatChannelSelectionProgressState.NoProgress -> binding.progress.visibility = View.GONE
                    ChatChannelSelectionProgressState.Progress   -> binding.progress.visibility = View.VISIBLE
                }
            }
            .addTo(destroyDisposables)

        viewModel.errorState
            .asErrorState(binding.rootContainer,
                          { viewModel.loadMainInfo() },
                          {
                              when (it) {
                                  ChatChannelSelectionErrorState.NoErrorState          -> ErrorState.NoError
                                  is ChatChannelSelectionErrorState.FatalErrorState    -> ErrorState.FatalError(it.error)
                                  is ChatChannelSelectionErrorState.NonFatalErrorState -> ErrorState.NonFatalError(it.error)
                              }
                          },
                          destroyDisposables)

        viewModel.dataState
            .toObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                when (it) {
                    is ChatChannelSelectionDataState.Data -> {
                        val newToolbarType = defaultToolbar.copy(content = ToolbarType.TitleContent(getString(R.string.title_chat_channel_selection,
                                                                                                              it.allChannelDescriptions.size)))
                        setToolbarType(newToolbarType)

                        binding.rvChannels.adapter = ChatChannelSelectionAdapter(viewModel, it.allChannelDescriptions)
                    }
                }
            }
            .addTo(destroyDisposables)
    }

    companion object {

        internal const val EXTRA_SELECTED_CHAT = "EXTRA_SELECTED_CHAT"
        internal const val EXTRA_ALL_CHATS = "EXTRA_ALL_CHATS"

        fun createScreen(context: Context,
                         selectedChatDescription: ChatChannelDescription,
                         allChatDescriptions: List<ChatChannelDescription>): Intent =
            Intent(context, ChatChannelSelectionActivity::class.java)
                .putExtra(EXTRA_SELECTED_CHAT, selectedChatDescription)
                .putExtra(EXTRA_ALL_CHATS, allChatDescriptions as Serializable)
    }
}