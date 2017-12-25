package io.scal.ambi.ui.home.chat.details

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.R
import io.scal.ambi.databinding.ActivityChatDetailsBinding
import io.scal.ambi.entity.chat.SmallChatItem
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.ToolbarType
import io.scal.ambi.extensions.view.listenForEndScrollInverse
import io.scal.ambi.ui.auth.profile.AuthProfileCheckerViewModel
import io.scal.ambi.ui.global.base.activity.BaseNavigator
import io.scal.ambi.ui.global.base.activity.BaseToolbarActivity
import io.scal.ambi.ui.global.picker.FileResource
import io.scal.ambi.ui.global.picker.PickerActionListener
import io.scal.ambi.ui.global.picker.PickerViewController
import io.scal.ambi.ui.global.picker.PickerViewModel
import io.scal.ambi.ui.home.chat.details.adapter.ChatDetailsAdapter
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

    private val pickerViewModel: PickerViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(PickerViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initPicker()
        initRecyclerView()
        initToolbar()
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
        val type = ToolbarType(IconImage(R.drawable.ic_back), Runnable { router.exit() }, ChatDetailsTitleToolbarContent(viewModel), null, null)
        type.makePin()
        setToolbarType(type)
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

        var snackBar: Snackbar? = null
        viewModel.errorState
            .toObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                snackBar?.dismiss()
                when (it) {
                    is ChatDetailsErrorState.NoErrorState       -> snackBar = null
                    is ChatDetailsErrorState.FatalErrorState    -> {
                        snackBar = Snackbar.make(binding.rootContainer, it.error, Snackbar.LENGTH_INDEFINITE)
                        snackBar!!.setAction(R.string.text_retry, { viewModel.loadMainInformation() })
                        snackBar!!.show()
                    }
                    is ChatDetailsErrorState.NonFatalErrorState ->
                        Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                }
            }
            .addTo(destroyDisposables)

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

    private fun initPicker() {
        binding.pickerListener = object : PickerActionListener {
            override fun attachPicture() {
                pickerViewModel.pickAnImage(this@ChatDetailsActivity, this@ChatDetailsActivity)
            }

            override fun attachFile() {
                pickerViewModel.pickFile(this@ChatDetailsActivity)
            }
        }
    }

    override fun setPickedFile(fileResource: FileResource, image: Boolean) {
        if (image) {
            viewModel.attachPicture(fileResource)
        } else {
            viewModel.attachFile(fileResource)
        }
    }

    override fun showPickerDialogFragment(dialogFragment: DialogFragment) {
        val ft = supportFragmentManager.beginTransaction()
        ft.add(dialogFragment, null)
        ft.commitAllowingStateLoss()
    }

    override fun askForReadExternalStoragePermission() {
        notifyPickerViewModelAboutPermissionWithPermissionCheck()
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    internal fun notifyPickerViewModelAboutPermission() {
        pickerViewModel.onReadExternalStoragePermissionGranted(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        pickerViewModel.onActivityResult(this, requestCode, resultCode, data)
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        onRequestPermissionsResult(requestCode, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override val navigator: Navigator by lazy {
        object : BaseNavigator(this) {
        }
    }

    companion object {

        internal val EXTRA_CHAT_UID = "EXTRA_CHAT_UID"
        internal val EXTRA_CHAT_INFO = "EXTRA_CHAT_INFO"

        fun createScreen(context: Context, chatUid: String): Intent =
            Intent(context, ChatDetailsActivity::class.java)
                .putExtra(EXTRA_CHAT_UID, chatUid)

        fun createScreen(context: Context, smallChatInfo: SmallChatItem): Intent =
            createScreen(context, smallChatInfo.uid)
                .putExtra(EXTRA_CHAT_INFO, smallChatInfo)
    }
}