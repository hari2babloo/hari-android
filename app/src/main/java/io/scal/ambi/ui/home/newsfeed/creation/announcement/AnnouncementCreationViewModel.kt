package io.scal.ambi.ui.home.newsfeed.creation.announcement

import android.databinding.ObservableField
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.binding.observable.ObservableString
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.interactor.home.newsfeed.creation.AnnouncementCreation
import io.scal.ambi.model.interactor.home.newsfeed.creation.IAnnouncementCreationInteractor
import io.scal.ambi.navigation.ResultCodes
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import io.scal.ambi.ui.home.newsfeed.creation.base.CreationBottomViewModel
import javax.inject.Inject

class AnnouncementCreationViewModel @Inject constructor(router: BetterRouter,
                                                        val bottomViewModel: CreationBottomViewModel,
                                                        private val interactor: IAnnouncementCreationInteractor,
                                                        private val rxSchedulersAbs: RxSchedulersAbs) : BaseViewModel(router) {

    val dataStateModel = ObservableField<AnnouncementCreationDataState>()
    val progressStateModel = ObservableField<AnnouncementCreationProgressState>(AnnouncementCreationProgressState.Progress)
    val errorStateModel = ObservableField<AnnouncementCreationErrorState>(AnnouncementCreationErrorState.NoError)

    init {
        loadAsUsers()
        observePostAction()
        observePostActionValidation()

        val audiences = interactor.availableAudiences.toMutableList()
        audiences.add(0, Audience.EVERYONE)
        bottomViewModel.updateAudiences(audiences)

        bottomViewModel.updateAnnouncementTypes(interactor.availableAnnouncementTypes)
    }

    fun reload() {
        if (errorStateModel.get() is AnnouncementCreationErrorState.ErrorFatal) {
            errorStateModel.set(AnnouncementCreationErrorState.NoError)
            loadAsUsers()
        }
    }


    fun changePinStatus() {
        val currentState = dataStateModel.get()
        if (currentState is AnnouncementCreationDataState.Data) {
            dataStateModel.set(currentState.copy(pinned = !currentState.pinned))
        }
    }

    fun changeLockStatus() {
        val currentState = dataStateModel.get()
        if (currentState is AnnouncementCreationDataState.Data) {
            dataStateModel.set(currentState.copy(locked = !currentState.locked))
        }
    }

    fun selectAsUser(user: User) {
        val currentState = dataStateModel.get()
        if (currentState is AnnouncementCreationDataState.Data) {
            dataStateModel.set(currentState.copy(selectedAsUser = user))
        }
    }

    private fun observePostAction() {
        bottomViewModel.postAction
            .observeOn(rxSchedulersAbs.mainThreadScheduler)
            .subscribe {
                val currentState = dataStateModel.get()
                if (currentState is AnnouncementCreationDataState.Data && progressStateModel.get() is AnnouncementCreationProgressState.NoProgress) {
                    progressStateModel.set(AnnouncementCreationProgressState.Progress)

                    val pollToCreate = AnnouncementCreation(currentState.pinned,
                                                            currentState.locked,
                                                            currentState.selectedAsUser,
                                                            currentState.announcementText.get(),
                                                            bottomViewModel.selectedAnnouncementType.get(),
                                                            bottomViewModel.selectedAudience.get(),
                                                            bottomViewModel.selectedAttachment.get()?.let { listOf(it) } ?: emptyList())

                    interactor.createAnnouncement(pollToCreate)
                        .compose(rxSchedulersAbs.ioToMainTransformerCompletable)
                        .subscribe({
                                       router.exitWithResult(ResultCodes.NEWS_FEED_ITEM_CREATED, null)
                                   },
                                   { t ->
                                       handleError(t)
                                       progressStateModel.set(AnnouncementCreationProgressState.NoProgress)
                                       errorStateModel.set(AnnouncementCreationErrorState.Error(t.message!!))
                                       errorStateModel.set(AnnouncementCreationErrorState.NoError)
                                   })
                        .addTo(disposables)
                }
            }
            .addTo(disposables)
    }

    private fun observePostActionValidation() {
        bottomViewModel.postEnable.set(false)

        dataStateModel.toObservable()
            .switchMap {
                if (it is AnnouncementCreationDataState.Data) {
                    it.announcementText.data
                        .toObservable()
                        .map { it.isNotBlank() }
                } else {
                    Observable.just(false)
                }
            }
            .distinctUntilChanged()
            .subscribe { bottomViewModel.postEnable.set(it) }
            .addTo(disposables)
    }

    private fun loadAsUsers() {
        interactor
            .loadAsUsers()
            .compose(rxSchedulersAbs.getIOToMainTransformerSingle())
            .subscribe(
                {
                    progressStateModel.set(AnnouncementCreationProgressState.NoProgress)
                    dataStateModel.set(AnnouncementCreationDataState.Data(false,
                                                                          false,
                                                                          it[0],
                                                                          it,
                                                                          ObservableString("")
                    ))
                },
                { t ->
                    handleError(t)
                    errorStateModel.set(AnnouncementCreationErrorState.ErrorFatal(t.message!!))
                })
            .addTo(disposables)
    }

    override fun onCleared() {
        super.onCleared()
        bottomViewModel.onCleared()
    }
}