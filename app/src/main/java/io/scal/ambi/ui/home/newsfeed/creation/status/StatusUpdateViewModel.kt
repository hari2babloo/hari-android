package io.scal.ambi.ui.home.newsfeed.creation.status

import android.databinding.ObservableField
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.binding.observable.ObservableString
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.interactor.home.newsfeed.creation.IStatusUpdateInteractor
import io.scal.ambi.model.interactor.home.newsfeed.creation.StatusUpdate
import io.scal.ambi.navigation.ResultCodes
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import io.scal.ambi.ui.home.newsfeed.creation.base.CreationBottomViewModel
import javax.inject.Inject

class StatusUpdateViewModel @Inject constructor(router: BetterRouter,
                                                val bottomViewModel: CreationBottomViewModel,
                                                private val interactor: IStatusUpdateInteractor,
                                                private val rxSchedulersAbs: RxSchedulersAbs) : BaseViewModel(router) {

    val dataStateModel = ObservableField<StatusUpdateDataState>()
    val progressStateModel = ObservableField<StatusUpdateProgressState>(StatusUpdateProgressState.Progress)
    val errorStateModel = ObservableField<StatusUpdateErrorState>(StatusUpdateErrorState.NoError)

    init {
        loadAsUsers()
        observePostAction()
        observePostActionValidation()

        val audiences = interactor.availableAudiences.toMutableList()
        audiences.add(0, Audience.EVERYONE)
        bottomViewModel.updateAudiences(audiences)
    }

    fun reload() {
        if (errorStateModel.get() is StatusUpdateErrorState.ErrorFatal) {
            errorStateModel.set(StatusUpdateErrorState.NoError)
            loadAsUsers()
        }
    }


    fun changePinStatus() {
        val currentState = dataStateModel.get()
        if (currentState is StatusUpdateDataState.Data) {
            dataStateModel.set(currentState.copy(pinned = !currentState.pinned))
        }
    }

    fun changeLockStatus() {
        val currentState = dataStateModel.get()
        if (currentState is StatusUpdateDataState.Data) {
            dataStateModel.set(currentState.copy(locked = !currentState.locked))
        }
    }

    fun selectAsUser(user: User) {
        val currentState = dataStateModel.get()
        if (currentState is StatusUpdateDataState.Data) {
            dataStateModel.set(currentState.copy(selectedAsUser = user))
        }
    }

    private fun observePostAction() {
        bottomViewModel.postAction
            .observeOn(rxSchedulersAbs.mainThreadScheduler)
            .subscribe {
                val currentState = dataStateModel.get()
                if (currentState is StatusUpdateDataState.Data && progressStateModel.get() is StatusUpdateProgressState.NoProgress) {
                    progressStateModel.set(StatusUpdateProgressState.Progress)

                    val pollToCreate = StatusUpdate(currentState.pinned,
                                                    currentState.locked,
                                                    currentState.selectedAsUser,
                                                    currentState.statusText.get(),
                                                    bottomViewModel.selectedAudience.get(),
                                                    bottomViewModel.selectedAttachment.get()?.let { listOf(it) } ?: emptyList())

                    interactor.updateStatus(pollToCreate)
                        .compose(rxSchedulersAbs.ioToMainTransformerCompletable)
                        .subscribe({
                                       router.exitWithResult(ResultCodes.NEWS_FEED_ITEM_CREATED, null)
                                   },
                                   { t ->
                                       handleError(t)
                                       progressStateModel.set(StatusUpdateProgressState.NoProgress)
                                       errorStateModel.set(StatusUpdateErrorState.Error(t.message!!))
                                       errorStateModel.set(StatusUpdateErrorState.NoError)
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
                if (it is StatusUpdateDataState.Data) {
                    it.statusText.data
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
                    progressStateModel.set(StatusUpdateProgressState.NoProgress)
                    dataStateModel.set(StatusUpdateDataState.Data(false,
                                                                  false,
                                                                  it[0],
                                                                  it,
                                                                  ObservableString("")
                    ))
                },
                { t ->
                    handleError(t)
                    errorStateModel.set(StatusUpdateErrorState.ErrorFatal(t.message!!))
                })
            .addTo(disposables)
    }


    override fun onCleared() {
        super.onCleared()
        bottomViewModel.onCleared()
    }
}