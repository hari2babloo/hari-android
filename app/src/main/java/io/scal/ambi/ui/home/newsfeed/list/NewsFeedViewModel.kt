package io.scal.ambi.ui.home.newsfeed.list

import android.content.Context
import android.databinding.ObservableField
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.entity.feed.*
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.binding.observable.OptimizedObservableArrayList
import io.scal.ambi.extensions.binding.replaceElement
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.interactor.home.newsfeed.INewsFeedInteractor
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.navigation.ResultCodes
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.base.viewmodel.BaseUserViewModel
import io.scal.ambi.ui.global.base.viewmodel.toGoodUserMessage
import io.scal.ambi.ui.global.model.DynamicUserChoicer
import io.scal.ambi.ui.global.model.PaginatorStateViewController
import io.scal.ambi.ui.global.model.createPaginator
import io.scal.ambi.ui.home.newsfeed.list.data.UIComments
import io.scal.ambi.ui.home.newsfeed.list.data.UILikes
import io.scal.ambi.ui.home.newsfeed.list.data.UIModelFeed
import ru.terrakok.cicerone.result.ResultListener
import javax.inject.Inject

class NewsFeedViewModel @Inject constructor(private val context: Context,
                                            router: BetterRouter,
                                            private val interactor: INewsFeedInteractor,
                                            rxSchedulersAbs: RxSchedulersAbs) :
    BaseUserViewModel(router, { interactor.loadCurrentUser() }, rxSchedulersAbs) {

    internal val progressState = ObservableField<NewsFeedProgressState>()
    internal val errorState = ObservableField<NewsFeedErrorState>()
    internal val dataState = ObservableField<NewsFeedDataState>()

    val selectedAudience = ObservableField<Audience>(Audience.COLLEGE_UPDATE)

    private val audienceSelectionListener = ResultListener { audience ->
        if (audience is Audience) {
            selectedAudience.set(audience)
        }
    }
    private val newsFeedItemCreationListener = ResultListener {
        if (it is NewsFeedItem) {
            // todo may be should add it here and do not do a full refresh?
            paginator.forceRefresh()
        }
    }

    private val paginator = createPaginator(
        { page -> executeLoadNextPage(page) },
        object : PaginatorStateViewController<UIModelFeed, NewsFeedProgressState, NewsFeedErrorState>(context, progressState, errorState) {

            override fun generateProgressEmptyState() = NewsFeedProgressState.EmptyProgress
            override fun generateProgressNoState() = NewsFeedProgressState.NoProgress
            override fun generateProgressRefreshState() = NewsFeedProgressState.RefreshProgress
            override fun generateProgressPageState() = NewsFeedProgressState.PageProgress

            override fun generateErrorFatal(message: String) = NewsFeedErrorState.FatalErrorState(message)
            override fun generateErrorNonFatal(message: String) = NewsFeedErrorState.NonFatalErrorState(message)
            override fun generateErrorNo() = NewsFeedErrorState.NoErrorState

            override fun showEmptyView(show: Boolean) {
                if (show) dataState.set(NewsFeedDataState.Empty)
            }

            override fun showData(show: Boolean, data: List<UIModelFeed>) {
                if (show) {
                    dataState.set(NewsFeedDataState.Data(OptimizedObservableArrayList(data)))
                }
            }
        },
        true
    )

    private val userLikeChoicer = DynamicUserChoicer<UIModelFeed>(rxSchedulersAbs,
                                                                  { uiModelFeed, action -> executeLikeAction(uiModelFeed, action) },
                                                                  { uiModelFeed -> uiModelFeed.uid })

    override fun onCurrentUserFetched(user: User) {
        super.onCurrentUserFetched(user)

        onInit()
    }

    fun changeAudience() {
        router.navigateTo(NavigateTo.CHANGE_AUDIENCE, selectedAudience.get())
    }

    fun createStatus() {
        router.navigateTo(NavigateTo.CREATE_STATUS)
    }

    fun createAnnouncement() {
        router.navigateTo(NavigateTo.CREATE_ANNOUNCEMENT)
    }

    fun createPoll() {
        router.navigateTo(NavigateTo.CREATE_POLL)
    }

    fun openAuthorOf(element: UIModelFeed) {
        if (element is UIModelFeed.Message) {
//            router.navigateTo(NavigateTo.PROFILE_DETAILS, element.author)
        }
    }

    fun changeUserLikeOf(element: UIModelFeed) {
        val currentDataState = dataState.get()
        if (currentDataState is NewsFeedDataState.Data) {
            if (currentDataState.newsFeed.contains(element)) {
                val newLikes =
                    if (element.likes.currentUserLiked) {
                        userLikeChoicer.changeUserChoice(element, DynamicUserChoicer.Action.NONE, DynamicUserChoicer.Action.LIKE)
                        element.likes.setupLike(currentUser.get(), false)
                    } else {
                        userLikeChoicer.changeUserChoice(element, DynamicUserChoicer.Action.LIKE, DynamicUserChoicer.Action.NONE)
                        element.likes.setupLike(currentUser.get(), true)
                    }

                currentDataState.newsFeed.replaceElement(element, element.changeLikes(newLikes))
            }
        }
    }

    fun openCommentsOf(element: UIModelFeed) {
//        router.navigateTo(NavigateTo.ALL_COMMENTS_OF, element)
    }

    fun selectPollChoice(element: UIModelFeed.Poll, choice: UIModelFeed.Poll.PollChoiceResult) {
        val currentDataState = dataState.get()
        if (currentDataState is NewsFeedDataState.Data) {
            if (currentDataState.newsFeed.contains(element)) {
                val newPollChoices = element
                    .choices
                    .map { it.pollChoice }
                    .map { if (it.uid == choice.pollChoice.uid) choice.pollChoice.copy(voters = choice.pollChoice.voters.plus(currentUser.get())) else it }

                currentDataState.newsFeed
                    .replaceElement(element, element.copy(choices = newPollChoices.toPollVotedResult(), userChoice = choice.pollChoice))

                interactor.answerForPoll(choice.pollChoice, element.uid)
                    .compose(rxSchedulersAbs.getIOToMainTransformerSingle())
                    .subscribe({ updatedElement ->
                                   val upToDateDataState = dataState.get()
                                   if (upToDateDataState is NewsFeedDataState.Data) {
                                       val listElement = currentDataState.newsFeed.firstOrNull { item -> item.uid == element.uid }

                                       if (null != listElement) {
                                           upToDateDataState.newsFeed.replaceElement(listElement, updatedElement.toNewsFeedElement(currentUser.get()))
                                       }
                                   }
                               },
                               { t ->
                                   handleError(t)

                                   val upToDateDataState = dataState.get()
                                   if (upToDateDataState is NewsFeedDataState.Data) {
                                       val listElement = currentDataState.newsFeed.firstOrNull { item -> item.uid == element.uid } as? UIModelFeed.Poll

                                       if (null != listElement) {
                                           val oldPollChoices = element.choices.map { it.pollChoice }.toPollVotedResult()

                                           upToDateDataState.newsFeed.replaceElement(listElement,
                                                                                     listElement.copy(choices = oldPollChoices, userChoice = null))
                                           errorState.set(NewsFeedErrorState.NonFatalErrorState(t.toGoodUserMessage(context)))
                                           errorState.set(NewsFeedErrorState.NoErrorState)
                                       }
                                   }
                               })
                    .addTo(disposables)
            }
        }
    }

    fun refresh() {
        paginator.refresh()
    }

    fun loadNextPage() {
        paginator.loadNewPage()
    }

    private fun onInit() {
        router.setResultListener(ResultCodes.AUDIENCE_SELECTION, audienceSelectionListener)
        router.setResultListener(ResultCodes.NEWS_FEED_ITEM_CREATED, newsFeedItemCreationListener)

        selectedAudience
            .toObservable()
            .distinctUntilChanged()
            .observeOn(rxSchedulersAbs.mainThreadScheduler)
            .subscribe { paginator.forceRefresh() }
            .addTo(disposables)

        observeLikeActions()
    }

    private fun executeLoadNextPage(page: Int): Single<List<UIModelFeed>> {
        return interactor
            .loadNewsFeedPage(page, if (1 == page) null else (dataState.get() as? NewsFeedDataState.Data)?.newsFeed?.last()?.createdAtDateTime)
            .subscribeOn(rxSchedulersAbs.ioScheduler)
            .observeOn(rxSchedulersAbs.computationScheduler)
            .flatMap {
                Observable.fromIterable(it)
                    .map<UIModelFeed> { it.toNewsFeedElement(currentUser.get()) }
                    .toList()
            }
            .observeOn(rxSchedulersAbs.mainThreadScheduler)
    }

    private fun executeLikeAction(uiModelFeed: UIModelFeed, action: DynamicUserChoicer.Action): Completable {
        return interactor
            .changeUserLikeForPost(uiModelFeed.feedItem, action == DynamicUserChoicer.Action.LIKE)
            .compose(rxSchedulersAbs.ioToMainTransformerCompletable)
    }

    private fun observeLikeActions() {
        userLikeChoicer
            .activate()
            .observeOn(rxSchedulersAbs.mainThreadScheduler)
            .subscribe {
                val currentDataState = dataState.get()
                if (currentDataState is NewsFeedDataState.Data) {
                    val element = currentDataState.newsFeed.firstOrNull { item -> item.uid == it.first.uid }

                    if (null != element) {
                        val newLikes = element.likes.setupLike(currentUser.get(), it.second == DynamicUserChoicer.Action.LIKE)
                        currentDataState.newsFeed.replaceElement(element, element.changeLikes(newLikes))
                    }
                }
            }
            .addTo(disposables)
    }

    override fun onCleared() {
        paginator.release()
        router.removeResultListener(ResultCodes.AUDIENCE_SELECTION, audienceSelectionListener)
        router.removeResultListener(ResultCodes.NEWS_FEED_ITEM_CREATED, newsFeedItemCreationListener)

        super.onCleared()
    }
}

private fun UILikes.setupLike(currentUser: User, like: Boolean): UILikes =
    if (like) {
        UILikes(currentUser, allUsersLiked.filter { it.uid != currentUser.uid }.plus(currentUser))
    } else {
        UILikes(currentUser, allUsersLiked.filter { it.uid != currentUser.uid })
    }

@Suppress("REDUNDANT_ELSE_IN_WHEN")
private fun UIModelFeed.changeLikes(newLikes: UILikes): UIModelFeed =
    when (this) {
        is UIModelFeed.Message -> copy(likes = newLikes)
        is UIModelFeed.Poll    -> copy(likes = newLikes)
        is UIModelFeed.Link    -> copy(likes = newLikes)
        else                   -> throw IllegalStateException("unknown element")
    }

private fun NewsFeedItem.toNewsFeedElement(currentUser: User): UIModelFeed =
    when (this) {
        is NewsFeedItemPoll         -> UIModelFeed.Poll(uid,
                                                        this,
                                                        user,
                                                        pollCreatedAt,
                                                        locked,
                                                        pinned,
                                                        null,
                                                        questionText,
                                                        choices.toPollVotedResult(),
                                                        choices.firstOrNull { null != it.voters.firstOrNull { it.uid == currentUser.uid } },
                                                        pollEndsTime,
                                                        UILikes(currentUser, likes),
                                                        UIComments(comments))
        is NewsFeedItemUpdate       -> UIModelFeed.Message(uid,
                                                           this,
                                                           user,
                                                           messageCreatedAt,
                                                           locked,
                                                           pinned,
                                                           null,
                                                           messageText,
                                                           UILikes(currentUser, likes),
                                                           UIComments(comments))
        is NewsFeedItemAnnouncement -> UIModelFeed.Message(uid,
                                                           this,
                                                           user,
                                                           messageCreatedAt,
                                                           locked,
                                                           pinned,
                                                           announcementType,
                                                           messageText,
                                                           UILikes(currentUser, likes),
                                                           UIComments(comments))
        else                        -> throw IllegalArgumentException("unknown NewsFeedItem: $this")
    }

private fun List<PollChoice>.toPollVotedResult(): List<UIModelFeed.Poll.PollChoiceResult> {
    val totalVotes = fold(0, { acc, pollChoice -> acc + pollChoice.voters.size })
    val mostVoted = fold(mutableListOf(), { acc: MutableList<PollChoice>, pollChoice ->
        when {
            acc.isEmpty()                                -> acc.add(pollChoice)
            acc[0].voters.size < pollChoice.voters.size  -> {
                acc.clear()
                acc.add(pollChoice)
            }
            acc[0].voters.size == pollChoice.voters.size -> acc.add(pollChoice)
        }
        acc
    })
    return map { UIModelFeed.Poll.PollChoiceResult(it, totalVotes, mostVoted.contains(it)) }
}