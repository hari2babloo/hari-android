package io.scal.ambi.ui.home.newsfeed.creation.base

import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.vanniktech.emoji.EmojiEditText
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import io.scal.ambi.entity.EmojiKeyboardState
import io.scal.ambi.entity.chat.ChatAttachment
import io.scal.ambi.entity.feed.AnnouncementType
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import io.scal.ambi.ui.global.picker.FileResource
import io.scal.ambi.ui.global.picker.PickerViewController
import io.scal.ambi.ui.global.picker.PickerViewModel
import io.scal.ambi.ui.home.chat.details.EmojiPopupHelper
import javax.inject.Inject

class CreationBottomViewModel @Inject constructor(router: BetterRouter) : BaseViewModel(router) {

    val attachmentActionsEnabled = ObservableBoolean(true)

    val announcementListVisibility = ObservableBoolean(false)
    val announcementList = ObservableArrayList<AnnouncementType>()
    val selectedAnnouncementType = ObservableField<AnnouncementType>()

    val audienceList = ObservableArrayList<Audience>()
    val selectedAudience = ObservableField<Audience>()

    var selectedAttachment = ObservableField<ChatAttachment>()

    val postEnable = ObservableBoolean(false)

    val postAction: Observable<Any> = PublishSubject.create()

    private val actionAttach = PublishSubject.create<Boolean>()
    private val actionKeyboardType = PublishSubject.create<Boolean>()

    fun post() {
        (postAction as Subject).onNext(Any())
    }

    fun updateAudiences(availableAudiences: List<Audience>) {
        updateListSelectionData(availableAudiences, audienceList, selectedAudience)
    }

    fun updateAnnouncementTypes(announcementTypes: List<AnnouncementType>) {
        updateListSelectionData(announcementTypes, announcementList, selectedAnnouncementType)
    }

    fun onAnnouncementTypeClicked(elementIndex: Int) {
        if (elementIndex >= 0 && elementIndex < announcementList.size) {
            selectedAnnouncementType.set(announcementList[elementIndex])
        }
    }

    fun switchAnnouncementTypesVisibility() {
        announcementListVisibility.set(!announcementListVisibility.get())
    }

    fun attachImage() {
        actionAttach.onNext(true)
    }

    fun attachFile() {
        actionAttach.onNext(false)
    }

    fun showEmoji() {
        actionKeyboardType.onNext(true)
    }

    fun showKeyboard() {
        actionKeyboardType.onNext(false)
    }

    fun initBottomActions(emojiEditText: EmojiEditText,
                          pickerViewModel: PickerViewModel,
                          pickerViewController: PickerViewController): Completable {
        val keyboardState = PublishSubject.create<EmojiKeyboardState>()
        val popupSubscription =
            EmojiPopupHelper()
                .activate(emojiEditText,
                          keyboardState
                              .observeOn(AndroidSchedulers.mainThread())
                )
                .doOnNext { actionKeyboardType.onNext(it == EmojiKeyboardState.EMOJI) }
        val compositeDisposable = CompositeDisposable()

        return Completable.ambArray(
            actionAttach
                .doOnNext {
                    if (it) pickerViewModel.pickAnImage(pickerViewController, emojiEditText.context)
                    else pickerViewModel.pickFile(pickerViewController)
                }
                .ignoreElements(),
            actionKeyboardType
                .doOnSubscribe { popupSubscription.subscribe().addTo(compositeDisposable) }
                .doOnDispose { compositeDisposable.dispose() }
                .doOnNext { if (it) keyboardState.onNext(EmojiKeyboardState.EMOJI) else keyboardState.onNext(EmojiKeyboardState.UNKNOWN) }
                .ignoreElements()
        )
    }

    fun setPickedFile(fileResource: FileResource, image: Boolean) {
        clearAttachments()
        selectedAttachment.set(if (image) ChatAttachment.LocalImage(fileResource) else ChatAttachment.LocalFile(fileResource))
    }

    public override fun onCleared() {
        super.onCleared()
        clearAttachments()
    }

    private fun clearAttachments() {
        selectedAttachment.get()?.
            apply {
                when (this) {
                    is ChatAttachment.LocalImage -> imageFile.cleanUp()
                    is ChatAttachment.LocalFile  -> fileFile.cleanUp()
                }
            }
        selectedAttachment.set(null)
    }

    private fun <T> updateListSelectionData(newData: List<T>,
                                            observableList: ObservableArrayList<T>,
                                            selectedItem: ObservableField<T>) {
        selectedItem.set(null)

        observableList.clear()
        observableList.addAll(newData)

        if (observableList.isNotEmpty()) {
            selectedItem.set(observableList.first())
        }
    }
}