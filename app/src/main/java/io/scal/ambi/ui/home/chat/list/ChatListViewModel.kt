package io.scal.ambi.ui.home.chat.list

import android.databinding.ObservableField
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.entity.User
import io.scal.ambi.entity.chat.SmallChatItem
import io.scal.ambi.extensions.binding.observable.OptimizedObservableArrayList
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.interactor.home.chat.IChatListInteractor
import io.scal.ambi.ui.global.base.viewmodel.BaseUserViewModel
import io.scal.ambi.ui.global.model.Paginator
import io.scal.ambi.ui.home.chat.list.data.ElementChatList
import io.scal.ambi.ui.home.chat.list.data.ElementChatListFilter
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class ChatListViewModel @Inject internal constructor(router: Router,
                                                     val searchViewModel: ChatSearchViewModel,
                                                     val interactor: IChatListInteractor,
                                                     rxSchedulersAbs: RxSchedulersAbs) :
    BaseUserViewModel(router, { interactor.loadCurrentUser() }, rxSchedulersAbs) {

    internal val progressState = ObservableField<ChatListProgressState>()
    internal val errorState = ObservableField<ChatListErrorState>()
    internal val filteredDataState = ObservableField<ChatListDataState>()

    private val allDataState = ObservableField<ChatListDataState>()

    internal val filterState = ObservableField<ChatListFilterState>()

    private val paginator = Paginator(
        { page -> loadNextPage(page) },
        object : Paginator.ViewController<ElementChatList> {
            override fun showEmptyProgress(show: Boolean) {
                if (show) progressState.set(ChatListProgressState.EmptyProgress)
                else progressState.set(ChatListProgressState.NoProgress)
            }

            override fun showEmptyError(show: Boolean, error: Throwable?) {
                if (show && null != error) {
                    errorState.set(ChatListErrorState.FatalErrorState(error))
                } else {
                    errorState.set(ChatListErrorState.NoErrorState)
                }
            }

            override fun showEmptyView(show: Boolean) {
                if (show) allDataState.set(ChatListDataState.Empty)
            }

            override fun showData(show: Boolean, data: List<ElementChatList>) {
                if (show) allDataState.set(ChatListDataState.Data(OptimizedObservableArrayList(data)))
            }

            override fun showErrorMessage(error: Throwable) {
                errorState.set(ChatListErrorState.NonFatalErrorState(error))
                errorState.set(ChatListErrorState.NoErrorState)
            }

            override fun showRefreshProgress(show: Boolean) {
                if (show) progressState.set(ChatListProgressState.RefreshProgress)
                else progressState.set(ChatListProgressState.NoProgress)
            }

            override fun showPageProgress(show: Boolean) {
                if (show) progressState.set(ChatListProgressState.PageProgress)
                else progressState.set(ChatListProgressState.NoProgress)
            }
        },
        true
    )

    override fun onCurrentUserFetched(user: User) {
        super.onCurrentUserFetched(user)

        paginator.activate()

        filterState.set(
            ChatListFilterState(listOf(ElementChatListFilter.AllChats, ElementChatListFilter.GroupChats, ElementChatListFilter.ClassChats),
                                ElementChatListFilter.AllChats))

        observerFilterChanges()

        paginator.refresh()
    }

    fun refresh() {
        paginator.refresh()
    }

    override fun onCleared() {
        paginator.release()

        super.onCleared()
    }

    private fun observerFilterChanges() {
        filterState
            .toObservable()
            .observeOn(rxSchedulersAbs.computationScheduler)
            .map {
                val currentAllDataState = allDataState.get()
                if (currentAllDataState is ChatListDataState.Data) {
                    val filteredData = it.filterAllData(currentAllDataState.chats)
                    if (filteredData.isNotEmpty()) {
                        return@map ChatListDataState.Data(filteredData)
                    }
                }
                ChatListDataState.Empty
            }
            .observeOn(rxSchedulersAbs.mainThreadScheduler)
            .subscribe { filteredDataState.set(it) }
            .addTo(disposables)

        allDataState
            .toObservable()
            .observeOn(rxSchedulersAbs.computationScheduler)
            .map {
                if (it is ChatListDataState.Data) {
                    filterState.get().filterAllData(it.chats)
                } else {
                    emptyList()
                }
            }
            .map { if (it.isEmpty()) ChatListDataState.Empty else ChatListDataState.Data(it) }
            .observeOn(rxSchedulersAbs.mainThreadScheduler)
            .subscribe { filteredDataState.set(it) }
            .addTo(disposables)
    }

    private fun loadNextPage(page: Int): Single<List<ElementChatList>> {
        return interactor.loadChatListPage(page)
            .flatMap {
                Observable.fromIterable(it)
                    .map { it.toChatListElement() }
                    .toList()
            }
            .compose(rxSchedulersAbs.getIOToMainTransformerSingle())
    }
}

private fun SmallChatItem.toChatListElement(): ElementChatList {
    return ElementChatList(uid,
                           icon,
                           title,
                           lastMessage,
                           lastMessageDateTime,
                           ElementChatListFilter.AllChats
    )
}

private fun ChatListFilterState.filterAllData(allChats: List<ElementChatList>): List<ElementChatList> {
    return allChats.filter { it.filterType == selectedFilter }
}
