package io.scal.ambi.ui.home.chat.details

import android.content.Context
import android.databinding.ObservableField
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.scal.ambi.R
import io.scal.ambi.entity.EmojiKeyboardState
import io.scal.ambi.entity.chat.*
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.appendCustom
import io.scal.ambi.extensions.binding.observable.ObservableString
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.model.interactor.home.chat.IChatDetailsInteractor
import io.scal.ambi.ui.global.base.viewmodel.BaseUserViewModel
import io.scal.ambi.ui.global.base.viewmodel.toGoodUserMessage
import io.scal.ambi.ui.global.model.Paginator
import io.scal.ambi.ui.global.model.createAppendablePaginator
import io.scal.ambi.ui.home.chat.details.data.*
import org.joda.time.format.DateTimeFormat
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.io.File
import java.net.URI
import java.text.DecimalFormat
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
        object : Paginator.ViewController<UIChatMessage> {
            override fun showEmptyProgress(show: Boolean) {
                if (show) progressState.set(ChatDetailsProgressState.EmptyDataProgress)
                else progressState.set(ChatDetailsProgressState.NoProgress)
            }

            override fun showEmptyError(show: Boolean, error: Throwable?) {
                if (show && null != error) {
                    errorState.set(ChatDetailsErrorState.FatalErrorState(error.toGoodUserMessage(context)))
                } else {
                    errorState.set(ChatDetailsErrorState.NoErrorState)
                }
            }

            override fun showEmptyView(show: Boolean) {
                if (show) serverDataListSubject.onNext(AllMessagesInfo(emptyList()))
            }

            override fun showData(show: Boolean, data: List<UIChatMessage>) {
                if (show) serverDataListSubject.onNext(AllMessagesInfo(data))
            }

            override fun showNoMoreData(show: Boolean, data: List<UIChatMessage>) {
                if (show) serverDataListSubject.onNext(AllMessagesInfo(data, dataState.get().chatInfo))
            }

            override fun showErrorMessage(error: Throwable) {
                errorState.set(ChatDetailsErrorState.NonFatalErrorState(error.toGoodUserMessage(context)))
                errorState.set(ChatDetailsErrorState.NoErrorState)
            }

            override fun showRefreshProgress(show: Boolean) {
                if (show) progressState.set(ChatDetailsProgressState.RefreshProgress)
                else progressState.set(ChatDetailsProgressState.NoProgress)
            }

            override fun showPageProgress(show: Boolean) {
                if (show) progressState.set(ChatDetailsProgressState.PageProgress)
                else progressState.set(ChatDetailsProgressState.NoProgress)
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

    fun loadNextPage() {
        paginator.loadNewPage()
    }

    fun attachPicture() {}

    fun attachFile() {}

    fun attachEmoji() {
        messageInputState.set(messageInputState.get().switchKeyboard())
    }

    fun updateEmojiState(emojiKeyboardState: EmojiKeyboardState) {
        messageInputState.set(messageInputState.get().copy(emojiKeyboardState = emojiKeyboardState))
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

private fun SmallChatItem?.toChatInfo(): UIChatInfo? =
    if (null == this) {
        null
    } else {
        UIChatInfo(icon, title, "")
    }

private fun FullChatItem.toChatInfo(context: Context): UIChatInfo {
    @Suppress("REDUNDANT_ELSE_IN_WHEN")
    val description =
        when (this) {
            is FullChatItem.Direct -> {
                val description = SpannableStringBuilder()
                description.append(context.getString(R.string.chat_details_info_direct))
                description.append(" ")
                description.appendCustom(otherUser.name, StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                description
            }
            is FullChatItem.Group  -> {
                val dateFormatter = DateTimeFormat.forPattern("MMMM dd, yyyy' at 'HH:mm a").withLocale(Locale.ENGLISH)
                val infoEndMessage = context.getString(R.string.chat_details_info_group_middle, dateFormatter.print(creationDateTime))
                val creatorName = creator.name.let { "\ufeff@$it" }
                val description = SpannableStringBuilder()
                description.appendCustom(creatorName,
                                         ForegroundColorSpan(ContextCompat.getColor(context, R.color.blueHref)),
                                         Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                description.append(" ")
                description.append(infoEndMessage)
                description
            }
            else                   -> throw IllegalArgumentException("unknown type: ${this.javaClass.simpleName}")
        }
    return UIChatInfo(icon, title, description)
}

private fun ChatMessage.toChatDetailsElement(currentUser: User): List<UIChatMessage> =
    when (this) {
        is ChatMessage.TextMessage       -> listOf(UIChatMessage.TextMessage(uid,
                                                                             sender,
                                                                             myMessageState.toMessageState(),
                                                                             message,
                                                                             sendDate,
                                                                             UIChatLikes(currentUser.uid, likes)))
        is ChatMessage.AttachmentMessage ->
            attachments.map {
                @Suppress("REDUNDANT_ELSE_IN_WHEN")
                when (it) {
                    is ChatAttachment.Image -> UIChatMessage.ImageMessage(uid,
                                                                          sender,
                                                                          myMessageState.toMessageState(),
                                                                          (message + "\n" + it.path.getFileName())
                                                                              .trim(),
                                                                          IconImage(it.path.toString()),
                                                                          sendDate,
                                                                          UIChatLikes(currentUser.uid, likes))
                    is ChatAttachment.File  -> UIChatMessage.AttachmentMessage(uid,
                                                                               sender,
                                                                               myMessageState.toMessageState(),
                                                                               it,
                                                                               (message + "\n" + it.path.getFileName()),
                                                                               "${it.size.getFileSize()} ${it.typeName}",
                                                                               sendDate,
                                                                               UIChatLikes(currentUser.uid, likes))
                    else                    -> throw IllegalArgumentException("unknown attachment type")
                }
            }
    }

private fun ChatMyMessageState?.toMessageState(): UIChatMessageStatus =
    when (this) {
        null                         -> UIChatMessageStatus.OTHER_USER_MESSAGE
        ChatMyMessageState.PENDING   -> UIChatMessageStatus.MY_MESSAGE_PENDING
        ChatMyMessageState.SEND      -> UIChatMessageStatus.MY_MESSAGE_SEND
        ChatMyMessageState.DELIVERED -> UIChatMessageStatus.MY_MESSAGE_DELIVERED
        ChatMyMessageState.READ      -> UIChatMessageStatus.MY_MESSAGE_READ
    }

private fun List<UIChatMessage>.addDateObjects(): List<Any> {
    // may be we should do it in BG thread.. for now lets do it here
    val result = fold(ArrayList<Any>(),
                      { acc, uiChatMessage ->
                          val lastItem = acc.lastOrNull()
                          if (lastItem is UIChatMessage) {
                              val lastItemMessageLocalDate = lastItem.messageDateTime.toLocalDate()
                              val currentMessageLocalDate = uiChatMessage.messageDateTime.toLocalDate()
                              if (lastItemMessageLocalDate.dayOfYear != currentMessageLocalDate.dayOfYear) {
                                  acc.add(UIChatDate(lastItemMessageLocalDate))
                              }
                          }
                          acc.add(uiChatMessage)
                          acc
                      }
    )
    if (result.isNotEmpty()) {
        val lastItem = result.last()
        if (lastItem is UIChatMessage) {
            result.add(UIChatDate(lastItem.messageDateTime.toLocalDate()))
        }
    }

    return result
}

private fun Long.getFileSize(): String {
    if (this <= 0)
        return "0"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(toDouble()) / Math.log10(1024.0)).toInt()
    return DecimalFormat("#,##0.#").format(this / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
}

private fun URI.getFileName(): String =
    try {
        File(path.toString()).name
    } catch (t: Throwable) {
        ""
    }

private data class AllMessagesInfo(val serverMessages: List<UIChatMessage>, val chatInfo: UIChatInfo? = null)