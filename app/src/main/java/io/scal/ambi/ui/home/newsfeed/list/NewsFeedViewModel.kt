package io.scal.ambi.ui.home.newsfeed.list

import android.content.Context
import android.databinding.ObservableField
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.entity.feed.FeedType
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
import io.scal.ambi.ui.home.newsfeed.list.adapter.INewsFeedViewModel
import io.scal.ambi.ui.home.newsfeed.list.data.UIModelFeed
import ru.terrakok.cicerone.result.ResultListener
import javax.inject.Inject

class NewsFeedViewModel @Inject constructor(private val context: Context,
                                            router: BetterRouter,
                                            private val interactor: INewsFeedInteractor,
                                            rxSchedulersAbs: RxSchedulersAbs) :
        BaseUserViewModel(router, { interactor.loadCurrentUser() }, rxSchedulersAbs),
        INewsFeedViewModel {

    internal val progressState = ObservableField<NewsFeedProgressState>()
    internal val errorState = ObservableField<NewsFeedErrorState>()
    internal val dataState = ObservableField<NewsFeedDataState>()

    var entityType = FeedType.MY_FEED.feedType;

    val selectedAudience = ObservableField<Audience>(Audience.STUDENTS)

    private val audienceSelectionListener = ResultListener { audience ->
        if (audience is Audience) {
            selectedAudience.set(audience)
        }
    }
    private val newsFeedItemCreationListener = ResultListener { paginator.forceRefresh() }

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

    private val userActions = NewsFeedViewModelActions(router,
            { uiModelFeed, action ->
                interactor
                        .changeUserLikeForPost(uiModelFeed.feedItem, action == DynamicUserChoicer.Action.LIKE)
            },
            { uiModelFeed, commentTet ->
                interactor.sendUserCommentToPost(uiModelFeed.feedItem, commentTet)
                        .doOnError {
                            handleError(it)

                            errorState.set(NewsFeedErrorState.NonFatalErrorState(it.toGoodUserMessage(context)))
                            errorState.set(NewsFeedErrorState.NoErrorState)
                        }
            },
            { uiModelFeed, pollChoiceResult ->
                interactor.answerForPoll(uiModelFeed.feedItem, pollChoiceResult.pollChoice)
                        .doOnError {
                            handleError(it)

                            errorState.set(NewsFeedErrorState.NonFatalErrorState(it.toGoodUserMessage(context)))
                            errorState.set(NewsFeedErrorState.NoErrorState)
                        }
            },
            {
                val currentState = dataState.get()
                if (currentState is NewsFeedDataState.Data) {
                    currentState.newsFeed
                } else {
                    null
                }
            },
            rxSchedulersAbs)

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

    override fun openAuthorOf(element: UIModelFeed) {
        userActions.openAuthorOf(element)
    }

    override fun openCommentsOf(element: UIModelFeed) {
        userActions.openCommentsOf(element)
    }

    override fun changeUserLikeOf(element: UIModelFeed) {
        val currentDataState = dataState.get()
        if (currentDataState is NewsFeedDataState.Data) {
            userActions.changeUserLikeOf(currentDataState.newsFeed, element, currentUser.get())
        }
    }

    override fun sendCommentForElement(element: UIModelFeed) {
        userActions.sendCommentForElement(element)
    }

    override fun selectPollChoice(element: UIModelFeed.Poll, choice: UIModelFeed.Poll.PollChoiceResult) {
        val currentDataState = dataState.get()
        if (currentDataState is NewsFeedDataState.Data) {
            userActions.selectPollChoice(currentDataState.newsFeed, element, choice, currentUser.get())
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

        paginator.activate()

        selectedAudience
                .toObservable()
                .distinctUntilChanged()
                .observeOn(rxSchedulersAbs.mainThreadScheduler)
                .subscribe { paginator.forceRefresh() }
                .addTo(disposables)

        observeLikeActions()
    }

    private fun executeLoadNextPage(page: Int): Single<List<UIModelFeed>> {
        if(entityType.equals(FeedType.OTHERS.feedType)){
            return interactor
                    .loadLatestNews()
                    .subscribeOn(rxSchedulersAbs.ioScheduler)
                    .observeOn(rxSchedulersAbs.computationScheduler)
                    .flatMap {
                        Observable.fromIterable(it)
                                .map<UIModelFeed> { it.toNewsFeedElement(currentUser.get()) }
                                .toList()
                    }
                    .observeOn(rxSchedulersAbs.mainThreadScheduler)
        }else{
            return interactor
                    .loadNewsFeedPage(entityType,page, selectedAudience.get())
                    .subscribeOn(rxSchedulersAbs.ioScheduler)
                    .observeOn(rxSchedulersAbs.computationScheduler)
                    .flatMap {
                        Observable.fromIterable(it)
                                .map<UIModelFeed> { it.toNewsFeedElement(currentUser.get()) }
                                .toList()
                    }
                    .observeOn(rxSchedulersAbs.mainThreadScheduler)
        }
    }

    private fun observeLikeActions() {
        userActions
                .observeLikes()
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