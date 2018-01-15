package io.scal.ambi.ui.home.chat.newmessage

import android.content.Context
import android.databinding.ObservableField
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.extensions.binding.observable.OptimizedObservableArrayList
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.interactor.home.chat.IChatNewMessageInteractor
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import io.scal.ambi.ui.global.base.viewmodel.toGoodUserMessage
import io.scal.ambi.ui.global.model.PaginatorStateViewController
import io.scal.ambi.ui.global.model.createPaginator
import javax.inject.Inject

class ChatNewMessageViewModel @Inject constructor(private val context: Context,
                                                  router: BetterRouter,
                                                  private val rxSchedulersAbs: RxSchedulersAbs,
                                                  private val interactor: IChatNewMessageInteractor) : BaseViewModel(router) {

    internal val progressState = ObservableField<ChatNewMessageProgressState>(ChatNewMessageProgressState.NoProgress)
    internal val errorState = ObservableField<ChatNewMessageErrorState>(ChatNewMessageErrorState.NoErrorState)
    internal val dataState = ObservableField<ChatNewMessageDataState>()

    private var userSelectionText = ""

    private val paginator = createPaginator(
        { page -> loadNextPage(page) },
        object : PaginatorStateViewController<UIUserChip, ChatNewMessageProgressState, ChatNewMessageErrorState>(context, progressState, errorState) {

            override fun generateProgressEmptyState() = ChatNewMessageProgressState.EmptyProgress
            override fun generateProgressNoState() = ChatNewMessageProgressState.NoProgress
            override fun generateProgressRefreshState() = ChatNewMessageProgressState.RefreshProgress
            override fun generateProgressPageState() = ChatNewMessageProgressState.PageProgress

            override fun generateErrorFatal(message: String) = ChatNewMessageErrorState.FatalErrorState(message)
            override fun generateErrorNonFatal(message: String) = ChatNewMessageErrorState.NonFatalErrorState(message)
            override fun generateErrorNo() = ChatNewMessageErrorState.NoErrorState

            override fun showEmptyView(show: Boolean) {
                if (show) {
                    val currentState = dataState.get()
                    if (currentState !is ChatNewMessageDataState.EmptyData) {
                        dataState.set(ChatNewMessageDataState.EmptyData())
                    }
                }
            }

            override fun showData(show: Boolean, data: List<UIUserChip>) {
                if (show) {
                    val currentState = dataState.get()
                    val newDataState =
                        if (currentState is ChatNewMessageDataState.Data) {
                            currentState.copy(users = data)
                        } else {
                            ChatNewMessageDataState.Data(OptimizedObservableArrayList(data))
                        }
                    dataState.set(newDataState)
                }
            }
        }
    )

    init {
        refresh()
    }

    fun refresh() = paginator.refresh()

    fun updateList(userText: String) {
        userSelectionText = userText
        paginator.forceRefresh(false)
    }

    fun createChat() {
        val currentDataState = dataState.get()
        if (currentDataState is ChatNewMessageDataState.Data && progressState.get() is ChatNewMessageProgressState.NoProgress) {
            progressState.set(ChatNewMessageProgressState.TotalProgress)

            interactor.createChat(currentDataState.selectedUsers.map { it.user })
                .compose(rxSchedulersAbs.getIOToMainTransformerSingle())
                .subscribe({
                               router.replaceScreen(NavigateTo.CHAT_DETAILS, it)
                           },
                           { t ->
                               handleError(t)

                               errorState.set(ChatNewMessageErrorState.NonFatalErrorState(t.toGoodUserMessage(context)))
                               errorState.set(ChatNewMessageErrorState.NoErrorState)

                               dataState.set(currentDataState)

                               progressState.set(ChatNewMessageProgressState.NoProgress)
                           })
                .addTo(disposables)
        }
    }

    private fun loadNextPage(page: Int): Single<List<UIUserChip>> {
        return interactor.loadUserWithPrefix(userSelectionText, page)
            .observeOn(rxSchedulersAbs.computationScheduler)
            .flatMap {
                Observable.fromIterable(it)
                    .map { UIUserChip(it) }
                    .toList()
            }
            .compose(rxSchedulersAbs.getIOToMainTransformerSingle())
    }
}