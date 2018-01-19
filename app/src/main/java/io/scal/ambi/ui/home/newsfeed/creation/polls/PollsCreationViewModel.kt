package io.scal.ambi.ui.home.newsfeed.creation.polls

import android.content.Context
import android.databinding.ObservableField
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.entity.feed.PollChoice
import io.scal.ambi.entity.feed.PollEndsTime
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.binding.observable.ObservableString
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.interactor.home.newsfeed.creation.IPollsCreationInteractor
import io.scal.ambi.model.interactor.home.newsfeed.creation.PollCreation
import io.scal.ambi.navigation.ResultCodes
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import io.scal.ambi.ui.global.base.viewmodel.toGoodUserMessage
import io.scal.ambi.ui.home.newsfeed.creation.base.CreationBottomViewModel
import javax.inject.Inject

class PollsCreationViewModel @Inject constructor(private val context: Context,
                                                 router: BetterRouter,
                                                 val bottomViewModel: CreationBottomViewModel,
                                                 private val interactor: IPollsCreationInteractor,
                                                 private val rxSchedulersAbs: RxSchedulersAbs) : BaseViewModel(router) {

    val dataStateModel = ObservableField<PollsCreationDataState>()
    val progressStateModel = ObservableField<PollsCreationProgressState>(PollsCreationProgressState.Progress)
    val errorStateModel = ObservableField<PollsCreationErrorState>(PollsCreationErrorState.NoError)

    private val allPollEnds = listOf(PollEndsTime.OneDay, PollEndsTime.OneWeek, PollEndsTime.UserCustomDefault, PollEndsTime.Never)

    init {
        loadAsUsers()
        observePostAction()
        observePostActionValidation()

        bottomViewModel.updateAudiences(emptyList())
        bottomViewModel.attachmentActionsEnabled.set(false)
    }

    fun reload() {
        if (errorStateModel.get() is PollsCreationErrorState.ErrorFatal) {
            errorStateModel.set(PollsCreationErrorState.NoError)
            loadAsUsers()
        }
    }


    fun changePinStatus() {
        val currentState = dataStateModel.get()
        if (currentState is PollsCreationDataState.Data) {
            dataStateModel.set(currentState.copy(pinned = !currentState.pinned))
        }
    }

    fun changeLockStatus() {
        val currentState = dataStateModel.get()
        if (currentState is PollsCreationDataState.Data) {
            dataStateModel.set(currentState.copy(locked = !currentState.locked))
        }
    }

    fun selectAsUser(user: User) {
        val currentState = dataStateModel.get()
        if (currentState is PollsCreationDataState.Data) {
            dataStateModel.set(currentState.copy(selectedAsUser = user))
        }
    }

    fun selectPollEnds(pollEndsTime: PollEndsTime) {
        val currentState = dataStateModel.get()
        if (currentState is PollsCreationDataState.Data) {
            val newPollDurations =
                if (pollEndsTime is PollEndsTime.UserCustom) {
                    currentState.pollDurations
                        .map { if (it is PollEndsTime.UserCustom) pollEndsTime else it }
                } else {
                    currentState.pollDurations
                }

            dataStateModel.set(currentState.copy(selectedPollDuration = pollEndsTime, pollDurations = newPollDurations))
        }
    }

    fun addNewChoice() {
        val currentState = dataStateModel.get()
        if (currentState is PollsCreationDataState.Data) {
            val newChoice = currentState.choices.toMutableList()
            newChoice.add(PollsCreationChoiceViewModel("", currentState.choices.size + 1))

            dataStateModel.set(currentState.copy(choices = newChoice))
        }
    }

    private fun observePostAction() {
        bottomViewModel.postAction
            .observeOn(rxSchedulersAbs.mainThreadScheduler)
            .subscribe {
                val currentState = dataStateModel.get()
                if (currentState is PollsCreationDataState.Data && progressStateModel.get() is PollsCreationProgressState.NoProgress) {
                    progressStateModel.set(PollsCreationProgressState.Progress)

                    val pollToCreate = PollCreation(currentState.pinned,
                                                    currentState.locked,
                                                    currentState.selectedAsUser,
                                                    currentState.questionText.get(),
                                                    currentState.choices.map { it.choiceInput.get() }.map { PollChoice.createNew(it) },
                                                    currentState.selectedPollDuration)

                    interactor.postPoll(pollToCreate)
                        .compose(rxSchedulersAbs.ioToMainTransformerCompletable)
                        .subscribe({
                                       router.exitWithResult(ResultCodes.NEWS_FEED_ITEM_CREATED, null)
                                   },
                                   { t ->
                                       handleError(t)
                                       progressStateModel.set(PollsCreationProgressState.NoProgress)
                                       errorStateModel.set(PollsCreationErrorState.Error(t.toGoodUserMessage(context)))
                                       errorStateModel.set(PollsCreationErrorState.NoError)
                                   })
                        .addTo(disposables)
                }
            }
            .addTo(disposables)
    }

    private fun observePostActionValidation() {
        bottomViewModel.postEnable.set(false)

        val choiceFillObservable = dataStateModel.toObservable()
            .switchMap {
                if (it is PollsCreationDataState.Data) {
                    val validations = it.choices.map { it.validate() }
                    Observable.combineLatest(validations, { t ->
                        t.fold(true, { acc, any -> acc && (any as Boolean) })
                    })
                } else {
                    Observable.just(false)
                }
            }
            .distinctUntilChanged()
        val questionFillObservable = dataStateModel.toObservable()
            .switchMap {
                if (it is PollsCreationDataState.Data) {
                    it.questionText.data
                        .toObservable()
                        .map { it.isNotBlank() }
                } else {
                    Observable.just(false)
                }
            }
            .distinctUntilChanged()

        Observable.combineLatest(choiceFillObservable,
                                 questionFillObservable,
                                 BiFunction<Boolean, Boolean, Boolean> { t1, t2 -> t1 && t2 })
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
                    progressStateModel.set(PollsCreationProgressState.NoProgress)
                    dataStateModel.set(PollsCreationDataState.Data(false,
                                                                   false,
                                                                   it[0],
                                                                   it,
                                                                   ObservableString(""),
                                                                   listOf(PollsCreationChoiceViewModel("", 1),
                                                                          PollsCreationChoiceViewModel("", 2)
                                                                   ),
                                                                   PollEndsTime.OneDay,
                                                                   allPollEnds
                    ))
                },
                { t ->
                    handleError(t)
                    errorStateModel.set(PollsCreationErrorState.ErrorFatal(t.message!!))
                })
            .addTo(disposables)
    }


    override fun onCleared() {
        super.onCleared()
        bottomViewModel.onCleared()
    }
}
