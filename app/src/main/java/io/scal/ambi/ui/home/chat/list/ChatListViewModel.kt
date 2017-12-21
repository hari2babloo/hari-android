package io.scal.ambi.ui.home.chat.list

import android.content.Context
import android.databinding.ObservableField
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.R
import io.scal.ambi.entity.user.User
import io.scal.ambi.entity.chat.ChatMessage
import io.scal.ambi.entity.chat.SmallChatItem
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.interactor.home.chat.IChatListInteractor
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.global.base.viewmodel.BaseUserViewModel
import io.scal.ambi.ui.global.model.Paginator
import io.scal.ambi.ui.home.chat.list.data.UIChatList
import io.scal.ambi.ui.home.chat.list.data.UIChatListFilter
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class ChatListViewModel @Inject internal constructor(private val context: Context,
                                                     router: Router,
                                                     val searchViewModel: ChatSearchViewModel,
                                                     val interactor: IChatListInteractor,
                                                     rxSchedulersAbs: RxSchedulersAbs) :
    BaseUserViewModel(router, { interactor.loadCurrentUser() }, rxSchedulersAbs) {

    internal val progressState = ObservableField<ChatListProgressState>()
    internal val errorState = ObservableField<ChatListErrorState>()
    internal val filteredDataState = ObservableField<ChatListDataState>()

    private val allDataState = ObservableField<ChatListDataState>()

    val filterModel = ChatFilterModel(listOf(UIChatListFilter.AllChats, UIChatListFilter.GroupChats, UIChatListFilter.ClassChats))

    private val paginator = Paginator(
        { page -> loadNextPage(page) },
        object : Paginator.ViewController<UIChatList> {
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

            override fun showData(show: Boolean, data: List<UIChatList>) {
                if (show) allDataState.set(ChatListDataState.Data(data))
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

        initChatTypeFilters()

        paginator.refresh()
    }

    fun refresh() {
        paginator.refresh()
    }

    fun loadNextPage() {
        paginator.loadNewPage()
    }

    fun openChatDetails(element: UIChatList) {
        router.navigateTo(NavigateTo.CHAT_DETAILS, element.chatInfo)
    }

    override fun onCleared() {
        paginator.release()

        super.onCleared()
    }

    private fun initChatTypeFilters() {
        filterModel.onFilterClicked(0)

        filterModel
            .selectedFilter
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
                    filterModel.selectedFilter.get().filterAllData(it.chats)
                } else {
                    emptyList()
                }
            }
            .map { if (it.isEmpty()) ChatListDataState.Empty else ChatListDataState.Data(it) }
            .observeOn(rxSchedulersAbs.mainThreadScheduler)
            .subscribe { filteredDataState.set(it) }
            .addTo(disposables)
    }

    private fun loadNextPage(page: Int): Single<List<UIChatList>> {
        return interactor.loadChatListPage(page)
            .subscribeOn(rxSchedulersAbs.ioScheduler)
            .observeOn(rxSchedulersAbs.computationScheduler)
            .flatMap {
                Observable.fromIterable(it)
                    .map { it.toChatListElement(context, currentUser.get()) }
                    .toList()
            }
            .observeOn(rxSchedulersAbs.mainThreadScheduler)
    }
}

private fun SmallChatItem.toChatListElement(context: Context, currentUser: User): UIChatList {
    val lastMessageSenderName: String? =
        when {
            null == lastMessage          -> null
            this is SmallChatItem.Direct -> {
                when {
                    lastMessage!!.sender.uid == currentUser.uid -> context.getString(R.string.chat_list_message_sender_you)
                    else                                        -> ""
                }
            }
            else                         -> {
                when {
                    lastMessage!!.sender.uid == currentUser.uid -> context.getString(R.string.chat_list_message_sender_you)
                    else                                        -> lastMessage!!.sender.name
                }
            }
        }?.let { if (it.isEmpty()) "" else "$it: " }


    return UIChatList(this,
                      when (lastMessage) {
                          null                             ->
                              context.getString(R.string.chat_list_message_empty)
                          is ChatMessage.TextMessage       ->
                              context.getString(R.string.chat_list_message_text, lastMessageSenderName, lastMessage!!.toMessageData(context))
                          is ChatMessage.AttachmentMessage ->
                              context.getString(R.string.chat_list_message_text, lastMessageSenderName, lastMessage!!.toMessageData(context))
                      }.trim(),
                      lastMessage?.sendDate ?: creationDateTime,
                      hasNewMessages,
                      UIChatListFilter.AllChats
    )
}

private fun ChatMessage.toMessageData(context: Context): String =
    when {
        message.isNotBlank()                  -> message
        this is ChatMessage.TextMessage       -> context.getString(R.string.chat_list_message_send_text)
        this is ChatMessage.AttachmentMessage -> context.getString(R.string.chat_list_message_send_attachment)
        else                                  -> throw IllegalArgumentException("unknown chat message type: $this")
    }

private fun UIChatListFilter?.filterAllData(allChats: List<UIChatList>): List<UIChatList> {
    return allChats.filter { it.filterType == this }
}