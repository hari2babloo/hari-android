package io.scal.ambi.ui.home.chat.new

import android.content.Context
import android.databinding.ObservableField
import io.reactivex.Single
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.binding.observable.ObservableString
import io.scal.ambi.extensions.binding.observable.OptimizedObservableArrayList
import io.scal.ambi.extensions.binding.replaceElements
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.interactor.home.chat.IChatNewMessageInteractor
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import io.scal.ambi.ui.global.model.PaginatorStateViewController
import io.scal.ambi.ui.global.model.createAppendablePaginator
import ru.terrakok.cicerone.Router
import java.util.concurrent.TimeUnit

class ChatNewMessageViewModel(context: Context,
                              router: Router,
                              private val rxSchedulersAbs: RxSchedulersAbs,
                              private val interactor: IChatNewMessageInteractor) : BaseViewModel(router) {

    internal val progressState = ObservableField<ChatNewMessageProgressState>(ChatNewMessageProgressState.NoProgress)
    internal val errorState = ObservableField<ChatNewMessageErrorState>(ChatNewMessageErrorState.NoErrorState)
    internal val dataState = ObservableField<ChatNewMessageDataState>()

    val inputUserName = ObservableString()

    private val paginator = createAppendablePaginator(
        { page -> loadNextPage(page) },
        object : PaginatorStateViewController<User, ChatNewMessageProgressState, ChatNewMessageErrorState>(context, progressState, errorState) {

            override fun generateProgressEmptyState() = ChatNewMessageProgressState.EmptyDataProgress
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

            override fun showData(show: Boolean, data: List<User>) {
                if (show) {
                    val currentState = dataState.get()
                    if (currentState is ChatNewMessageDataState.Data) {
                        currentState.users.replaceElements(data)
                    } else {
                        dataState.set(ChatNewMessageDataState.Data(OptimizedObservableArrayList(data)))
                    }
                }
            }
        }
    )

    init {
        refresh()

        observeUserInput()
    }

    fun refresh() = paginator.refresh()

    fun loadNextPage() = paginator.loadNewPage()

    private fun loadNextPage(page: Int): Single<List<User>> {
        return interactor.loadUserWithPrefix(inputUserName.data.get(), page)
            .compose(rxSchedulersAbs.getIOToMainTransformerSingle())
    }

    private fun observeUserInput() {
        inputUserName.data
            .toObservable()
            .debounce(600, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .observeOn(rxSchedulersAbs.mainThreadScheduler)
            .subscribe { paginator.forceRefresh() }
            .addTo(disposables)
    }
}