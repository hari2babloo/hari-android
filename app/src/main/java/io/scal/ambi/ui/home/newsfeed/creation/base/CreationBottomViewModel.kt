package io.scal.ambi.ui.home.newsfeed.creation.base

import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import io.scal.ambi.entity.feed.AnnouncementType
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import javax.inject.Inject

class CreationBottomViewModel @Inject constructor(router: BetterRouter) : BaseViewModel(router) {

    val attachmentActionsEnabled = ObservableBoolean(true)

    val announcementListVisibility = ObservableBoolean(false)
    val announcementList = ObservableArrayList<AnnouncementType>()
    val selectedAnnouncementType = ObservableField<AnnouncementType>()

    val audienceList = ObservableArrayList<Audience>()
    val selectedAudience = ObservableField<Audience>()

    val postEnable = ObservableBoolean(false)

    val postAction: Observable<Any> = PublishSubject.create()

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

    }

    fun attachFile() {

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