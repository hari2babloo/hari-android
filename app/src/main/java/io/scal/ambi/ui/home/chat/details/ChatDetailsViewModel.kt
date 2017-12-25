package io.scal.ambi.ui.home.chat.details

import android.content.Context
import android.databinding.ObservableField
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.scal.ambi.entity.EmojiKeyboardState
import io.scal.ambi.entity.chat.SmallChatItem
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.binding.observable.ObservableString
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.interactor.home.chat.IChatDetailsInteractor
import io.scal.ambi.ui.global.base.viewmodel.BaseUserViewModel
import io.scal.ambi.ui.global.base.viewmodel.toGoodUserMessage
import io.scal.ambi.ui.global.model.PaginatorStateViewController
import io.scal.ambi.ui.global.model.createAppendablePaginator
import io.scal.ambi.ui.global.picker.FileResource
import io.scal.ambi.ui.home.chat.details.data.UIChatMessage
import io.scal.ambi.ui.home.chat.details.data.UIChatMessageStatus
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class ChatDetailsViewModel @Inject constructor(private val context: Context,
                                               router: Router,
                                               @Named("chatInfo") smallChatItem: SmallChatItem?,
                                               private val interactor: IChatDetailsInteractor,
                                               rxSchedulersAbs: RxSchedulersAbs) :
    BaseUserViewModel(router, { interactor.loadCurrentUser() }, rxSchedulersAbs) {

    internal val progressState = ObservableField<ChatDetailsProgressState>(ChatDetailsProgressState.NoProgress)
    internal val errorState = ObservableField<ChatDetailsErrorState>(ChatDetailsErrorState.NoErrorState)
    internal val dataState = ObservableField<ChatDetailsDataState>(ChatDetailsDataState.Initial(smallChatItem.toChatInfo()))
    val messageInputState = ObservableField<MessageInputState>(MessageInputState())

    private val serverDataListSubject = PublishSubject.create<AllMessagesInfo>()
    private val pendingMessagesSubject = BehaviorSubject.createDefault(emptyList<UIChatMessage>())

    private val paginator = createAppendablePaginator(
        { page -> loadNextPage(page) },
        object : PaginatorStateViewController<UIChatMessage, ChatDetailsProgressState, ChatDetailsErrorState>(context, progressState, errorState) {

            override fun generateProgressEmptyState() = ChatDetailsProgressState.EmptyDataProgress
            override fun generateProgressNoState() = ChatDetailsProgressState.NoProgress
            override fun generateProgressRefreshState() = ChatDetailsProgressState.RefreshProgress
            override fun generateProgressPageState() = ChatDetailsProgressState.PageProgress

            override fun generateErrorFatal(message: String) = ChatDetailsErrorState.FatalErrorState(message)
            override fun generateErrorNonFatal(message: String) = ChatDetailsErrorState.NonFatalErrorState(message)
            override fun generateErrorNo() = ChatDetailsErrorState.NoErrorState

            override fun showEmptyView(show: Boolean) {
                if (show) serverDataListSubject.onNext(AllMessagesInfo(emptyList()))
            }

            override fun showData(show: Boolean, data: List<UIChatMessage>) {
                if (show) serverDataListSubject.onNext(AllMessagesInfo(data))
            }

            override fun showNoMoreData(show: Boolean, data: List<UIChatMessage>) {
                if (show) serverDataListSubject.onNext(AllMessagesInfo(data, dataState.get().chatInfo))
            }
        },
        true
    )

    override fun onCurrentUserFetched(user: User) {
        super.onCurrentUserFetched(user)

        Observable.combineLatest(serverDataListSubject
                                     .observeOn(rxSchedulersAbs.computationScheduler),
                                 pendingMessagesSubject
                                     .observeOn(rxSchedulersAbs.computationScheduler),
                                 BiFunction<AllMessagesInfo, List<UIChatMessage>, AllMessagesInfo> { t1, t2 ->
                                     AllMessagesInfo(t1.serverMessages.plus(t2), t1.chatInfo)
                                 })
            .subscribeOn(rxSchedulersAbs.computationScheduler)
            .observeOn(rxSchedulersAbs.computationScheduler)
            .doOnNext { Collections.sort(it.serverMessages, { o1, o2 -> o2.messageDateTime.compareTo(o1.messageDateTime) }) }
            .map {
                val messagesWithData = it.serverMessages.addDateObjects()
                if (null == it.chatInfo) messagesWithData else messagesWithData.plus(it.chatInfo)
            }
            .observeOn(rxSchedulersAbs.mainThreadScheduler)
            .subscribe { dataState.set(dataState.get().moveToData(it)) }
            .addTo(disposables)

        interactor.loadTypingInfo()
            .subscribeOn(rxSchedulersAbs.ioScheduler)
            .doOnError { Timber.e(it, "error during getting typing information. recovering...") }
            .retry()
            .observeOn(rxSchedulersAbs.mainThreadScheduler)
            .subscribe {
                val currentDataState = dataState.get()
                dataState.set(if (it.typing) currentDataState.startTyping(it.user) else currentDataState.stopTyping(it.user))
            }
            .addTo(disposables)

        interactor.loadSendingMessagesInfo()
            .subscribeOn(rxSchedulersAbs.ioScheduler)
            .doOnError { Timber.e(it, "error during getting sending messages. recovering...") }
            .retry()
            .observeOn(rxSchedulersAbs.computationScheduler)
            .flatMapSingle {
                val currentUser = currentUser.get()
                Observable.fromIterable(it)
                    .concatMap { Observable.fromIterable(it.toChatDetailsElement(currentUser)) }
                    .toList()
            }
            .observeOn(rxSchedulersAbs.mainThreadScheduler)
            .subscribe { pendingMessagesSubject.onNext(it) }
            .addTo(disposables)

        dataState.toObservable()
            .subscribeOn(rxSchedulersAbs.mainThreadScheduler)
            .observeOn(rxSchedulersAbs.mainThreadScheduler)
            .filter { it is ChatDetailsDataState.Data }
            .firstOrError()
            .retry()
            .subscribe(Consumer { loadNewMessages() })
            .addTo(disposables)

        loadMainInformation()
    }

    fun loadMainInformation() {
        if (progressState.get() is ChatDetailsProgressState.NoProgress) {
            if (dataState.get() is ChatDetailsDataState.Initial) {
                progressState.set(ChatDetailsProgressState.EmptyInfoProgress)

                interactor.loadChatInfo()
                    .compose(rxSchedulersAbs.getIOToMainTransformer())
                    .subscribe({
                                   progressState.set(ChatDetailsProgressState.NoProgress)
                                   dataState.set(dataState.get().updateInfo(it.toChatInfo(context)))

                                   paginator.activate()
                                   paginator.refresh()
                               },
                               { t ->
                                   handleError(t)

                                   progressState.set(ChatDetailsProgressState.NoProgress)
                                   errorState.set(ChatDetailsErrorState.FatalErrorState(t.toGoodUserMessage(context)))
                               })
                    .addTo(disposables)
            } else {
                paginator.refresh()
            }
        }
    }

    fun loadNextPage() = paginator.loadNewPage()

    fun attachPicture(fileResource: FileResource) {
        interactor.sendPictureMessage(fileResource)
    }

    fun attachFile(fileResource: FileResource) {
        interactor.sendFileMessage(fileResource)
    }

    fun attachEmoji() {
        messageInputState.set(messageInputState.get().switchKeyboard())
    }

    fun updateEmojiState(emojiKeyboardState: EmojiKeyboardState) {
        messageInputState.set(messageInputState.get().copy(emojiKeyboardState = emojiKeyboardState))
    }

    fun onMessageClick(element: UIChatMessage) {
        if (UIChatMessageStatus.MY_MESSAGE_SEND_FAILED == element.state) {
            interactor.resendMessage(element.uid)
        }
    }

    fun sendMessage() {
        val currentDataState = messageInputState.get()
        val message = currentDataState.userInput.get().trim()
        if (message.isNotEmpty()) {
            interactor.sendTextMessage(message)

            messageInputState.set(currentDataState.copy(userInput = ObservableString()))
        }
    }

    private fun loadNextPage(page: Int): Single<List<UIChatMessage>> {
        return interactor.loadChatPage(page)
            .subscribeOn(rxSchedulersAbs.ioScheduler)
            .observeOn(rxSchedulersAbs.computationScheduler)
            .flatMap {
                val user = currentUser.get()
                Observable.fromIterable(it)
                    .concatMap { Observable.fromIterable(it.toChatDetailsElement(user)) }
                    .toList()
            }
            .observeOn(rxSchedulersAbs.mainThreadScheduler)
    }

    private fun loadNewMessages() {
        interactor.loadNewMessages()
            .doOnError { Timber.e(it, "error during getting new messages. recovering...") }
            .retry()
            .observeOn(rxSchedulersAbs.computationScheduler)
            .flatMapSingle {
                val currentUser = currentUser.get()
                Observable.fromIterable(it)
                    .concatMap { Observable.fromIterable(it.toChatDetailsElement(currentUser)) }
                    .toList()
            }
            .observeOn(rxSchedulersAbs.mainThreadScheduler)
            .subscribe { paginator.appendNewData(it) }
            .addTo(disposables)
    }
}