package io.scal.ambi.ui.home.chat.details

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import com.ambi.work.R
import com.ambi.work.databinding.ActivityChatDetailsBinding
import io.scal.ambi.entity.chat.ChatChannelDescription
import io.scal.ambi.entity.chat.PreviewChatItem
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.ToolbarType
import io.scal.ambi.extensions.view.listenForEndScrollInverse
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.navigation.NavigateToParamChatAppendUsers
import io.scal.ambi.navigation.NavigateToParamChatChannelSelection
import io.scal.ambi.ui.auth.profile.AuthProfileCheckerViewModel
import io.scal.ambi.ui.global.base.ErrorState
import io.scal.ambi.ui.global.base.activity.BaseNavigator
import io.scal.ambi.ui.global.base.activity.BaseToolbarActivity
import io.scal.ambi.ui.global.base.asErrorState
import io.scal.ambi.ui.global.picker.FileResource
import io.scal.ambi.ui.global.picker.PickerActionListener
import io.scal.ambi.ui.global.picker.PickerViewController
import io.scal.ambi.ui.global.picker.PickerViewModel
import io.scal.ambi.ui.home.chat.channel.ChatChannelSelectionActivity
import io.scal.ambi.ui.home.chat.details.adapter.ChatDetailsAdapter
import io.scal.ambi.ui.home.chat.newmessage.ChatNewMessageActivity
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import ru.terrakok.cicerone.Navigator
import kotlin.reflect.KClass

@RuntimePermissions
class ChatDetailsActivity : BaseToolbarActivity<ChatDetailsViewModel, ActivityChatDetailsBinding>(), PickerViewController {

    override val layoutId: Int = R.layout.activity_chat_details
    override val viewModelClass: KClass<ChatDetailsViewModel> = ChatDetailsViewModel::class

    private val adapter by lazy { ChatDetailsAdapter(viewModel) }
    private val emojiPopupHelper by lazy { EmojiPopupHelper() }

    private lateinit var currentToolbar: ToolbarType

    private val pickerViewModel: PickerViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(PickerViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initToolbar()
        initCreationListeners()
        initRecyclerView()
        observeStates()

        ViewModelProviders.of(this, viewModelFactory).get(AuthProfileCheckerViewModel::class.java)
    }

    private fun initRecyclerView() {
        binding.rvMessages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        binding.rvMessages.adapter = adapter

        binding.rvMessages.listenForEndScrollInverse(1)
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { viewModel.loadNextPage() }
            .addTo(destroyDisposables)
    }

    private fun initToolbar() {
        currentToolbar = ToolbarType(IconImage(R.drawable.ic_back), Runnable { router.exit() }, ChatDetailsTitleToolbarContent(viewModel), null, null)
        setToolbarType(currentToolbar)
    }

    private fun observeStates() {
        viewModel.progressState
            .toObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                adapter.showPageProgress(false)
                binding.progress.visibility = View.GONE

                when (it) {
                    is ChatDetailsProgressState.EmptyInfoProgress -> binding.progress.visibility = View.VISIBLE
                    is ChatDetailsProgressState.EmptyDataProgress -> binding.progress.visibility = View.VISIBLE
                    is ChatDetailsProgressState.PageProgress      -> adapter.showPageProgress(true)
                    is ChatDetailsProgressState.RefreshProgress   -> binding.progress.visibility = View.VISIBLE
                    is ChatDetailsProgressState.NoProgress        -> binding.progress.visibility = View.GONE
                }
            }
            .addTo(destroyDisposables)

        viewModel.errorState.asErrorState(binding.rootContainer,
                                          { viewModel.loadMainInformation() },
                                          {
                                              when (it) {
                                                  is ChatDetailsErrorState.NoErrorState       -> ErrorState.NoError
                                                  is ChatDetailsErrorState.NonFatalErrorState -> ErrorState.NonFatalError(it.error)
                                                  is ChatDetailsErrorState.FatalErrorState    -> ErrorState.FatalError(it.error)
                                              }
                                          },
                                          destroyDisposables)
    }

    private fun initCreationListeners() {
        binding.pickerListener = object : PickerActionListener {
            override fun attachPicture() = pickerViewModel.pickAnImage(this@ChatDetailsActivity, this@ChatDetailsActivity)
            override fun attachFile() = pickerViewModel.pickFile(this@ChatDetailsActivity)
        }
        binding.zoomListener = object : MessageCreationZoomListener {
            override fun toggleZoom() = doMessageCreationViewZoomToggle()
        }

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

                currentToolbar =
                    if (true == it.chatInfo?.canAddUsers) {
                        currentToolbar.copy(rightIcon = IconImage(R.drawable.ic_chat_add_user),
                                            rightIconClickListener = Runnable { viewModel.addOtherUsers() })
                    } else {
                        currentToolbar.copy(rightIcon = null, rightIconClickListener = null)
                    }
                setToolbarType(currentToolbar)
            }
            .addTo(destroyDisposables)

        emojiPopupHelper
            .activate(binding.cCreation!!.etMessage,
                      viewModel.messageInputState
                          .toObservable()
                          .map { it.emojiKeyboardState }
                          .observeOn(AndroidSchedulers.mainThread())
            )
            .subscribe { viewModel.updateEmojiState(it) }
            .addTo(destroyDisposables)
    }

    override fun onBackPressed() {
        if (binding.cCreation!!.root.parent != binding.cData) {
            binding.zoomListener?.toggleZoom()
            return
        }
        super.onBackPressed()
    }

    override fun setPickedFile(fileResource: FileResource, image: Boolean) {
        if (image) viewModel.attachPicture(fileResource) else viewModel.attachFile(fileResource)
    }

    override fun showPickerDialogFragment(dialogFragment: DialogFragment) {
        val ft = supportFragmentManager.beginTransaction()
        ft.add(dialogFragment, null)
        ft.commitAllowingStateLoss()
    }

    override fun askForReadExternalStoragePermission() = notifyPickerViewModelAboutPermissionWithPermissionCheck()

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    internal fun notifyPickerViewModelAboutPermission() = pickerViewModel.onReadExternalStoragePermissionGranted(this)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        pickerViewModel.onActivityResult(this, requestCode, resultCode, data)
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        onRequestPermissionsResult(requestCode, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun doMessageCreationViewZoomToggle() {
        // real fucking bad function. ContraintLayout doesn't work as expected with runtime changes, making two view makes emoji broken, so...
        // do this!

        val creationBinding = binding.cCreation!!
        if (creationBinding.root.parent == binding.cData) {
            binding.cData.removeView(creationBinding.root)

            creationBinding.root.setBackgroundResource(R.drawable.bg_item_creation_big)
            creationBinding.ivZoom.setImageResource(R.drawable.ic_zoom_out)
            creationBinding.etMessage.maxLines = Integer.MAX_VALUE

            creationBinding.etMessage.layoutParams.run {
                height = ViewGroup.LayoutParams.MATCH_PARENT
                creationBinding.etMessage.layoutParams = this
            }
            (creationBinding.cEmoji.layoutParams as LinearLayout.LayoutParams).run {
                height = 0
                weight = 1f
                creationBinding.cEmoji.layoutParams = this
            }

            binding.rootContainer
                .addView(creationBinding.root,
                         RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        } else {
            binding.rootContainer.removeView(creationBinding.root)

            creationBinding.root.setBackgroundColor(ContextCompat.getColor(this@ChatDetailsActivity, R.color.white))
            creationBinding.ivZoom.setImageResource(R.drawable.ic_zoom_in)
            creationBinding.etMessage.maxLines = 3

            creationBinding.etMessage.layoutParams.run {
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                creationBinding.etMessage.layoutParams = this
            }
            (creationBinding.cEmoji.layoutParams as LinearLayout.LayoutParams).run {
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                weight = 0f
                creationBinding.cEmoji.layoutParams = this
            }

            val rootLp = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            rootLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            binding.cData.addView(creationBinding.root, rootLp)
        }
        creationBinding.etMessage.requestFocus()
    }

    override val navigator: Navigator by lazy {
        object : BaseNavigator(this) {
            override fun createActivityIntent(screenKey: String, data: Any?): Intent? =
                when (screenKey) {
                    NavigateTo.CHAT_CHANNEL_SELECTION -> {
                        data as NavigateToParamChatChannelSelection
                        ChatChannelSelectionActivity.createScreen(this@ChatDetailsActivity,
                                                                  data.selectedChatDescription,
                                                                  data.allChatDescriptions)
                    }
                    NavigateTo.CHAT_DETAILS           -> createScreen(this@ChatDetailsActivity, data as ChatChannelDescription)
                    NavigateTo.CHAT_APPEND_USERS      -> {
                        data as NavigateToParamChatAppendUsers
                        ChatNewMessageActivity.createScreenForAppending(this@ChatDetailsActivity, data.chatDescription, data.currentMemebers)
                    }
                    else                              -> super.createActivityIntent(screenKey, data)
                }
        }
    }

    companion object {

        internal val EXTRA_CHAT_DESCRIPTION = "EXTRA_CHAT_DESCRIPTION"
        internal val EXTRA_CHAT_INFO = "EXTRA_CHAT_INFO"

        fun createScreen(context: Context, previewChatInfo: PreviewChatItem): Intent =
            createScreen(context, previewChatInfo.description)
                .putExtra(EXTRA_CHAT_INFO, previewChatInfo)

        private fun createScreen(context: Context, chatDescription: ChatChannelDescription): Intent =
            Intent(context, ChatDetailsActivity::class.java)
                .putExtra(EXTRA_CHAT_DESCRIPTION, chatDescription)
    }
}