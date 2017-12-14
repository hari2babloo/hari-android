package io.scal.ambi.ui.home.newsfeed.list

import android.databinding.ObservableField
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.entity.User
import io.scal.ambi.entity.actions.Comment
import io.scal.ambi.entity.feed.*
import io.scal.ambi.extensions.binding.observable.OptimizedObservableArrayList
import io.scal.ambi.extensions.binding.replaceElement
import io.scal.ambi.extensions.binding.replaceElements
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.interactor.home.newsfeed.INewsFeedInteractor
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.navigation.ResultCodes
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import io.scal.ambi.ui.global.model.Paginator
import io.scal.ambi.ui.home.newsfeed.list.data.ElementComments
import io.scal.ambi.ui.home.newsfeed.list.data.ElementLikes
import io.scal.ambi.ui.home.newsfeed.list.data.ElementModelFeed
import org.joda.time.DateTime
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

class NewsFeedViewModel @Inject constructor(router: Router,
                                            private val interactor: INewsFeedInteractor,
                                            private val rxSchedulersAbs: RxSchedulersAbs) : BaseViewModel(router) {

    internal val progressState = ObservableField<NewsFeedProgressState>()
    internal val errorState = ObservableField<NewsFeedErrorState>()
    internal val dataState = ObservableField<NewsFeedDataState>()

    val currentUser = ObservableField<User>()
    val selectedAudience = ObservableField<Audience>(Audience.COLLEGE_UPDATE)

    private val paginator = Paginator(
        { page -> loadNextPage(page) },
        object : Paginator.ViewController<ElementModelFeed> {
            override fun showEmptyProgress(show: Boolean) {
                if (show) progressState.set(NewsFeedProgressState.EmptyProgress)
                else progressState.set(NewsFeedProgressState.NoProgress)
            }

            override fun showEmptyError(show: Boolean, error: Throwable?) {
                if (show && null != error) {
                    errorState.set(NewsFeedErrorState.FatalErrorState(error))
                } else {
                    errorState.set(NewsFeedErrorState.NoErrorState)
                }
            }

            override fun showEmptyView(show: Boolean) {
                if (show) dataState.set(NewsFeedDataState.Empty)
            }

            override fun showData(show: Boolean, data: List<ElementModelFeed>) {
                if (show) {
                    val currentState = dataState.get()
                    if (currentState is NewsFeedDataState.Data) {
                        currentState.newsFeed.replaceElements(data)
                    } else {
                        dataState.set(NewsFeedDataState.Data(OptimizedObservableArrayList(data)))
                    }
                }
            }

            override fun showErrorMessage(error: Throwable) {
                errorState.set(NewsFeedErrorState.NonFatalErrorState(error))
                errorState.set(NewsFeedErrorState.NoErrorState)
            }

            override fun showRefreshProgress(show: Boolean) {
                if (show) progressState.set(NewsFeedProgressState.RefreshProgress)
                else progressState.set(NewsFeedProgressState.NoProgress)
            }

            override fun showPageProgress(show: Boolean) {
                if (show) progressState.set(NewsFeedProgressState.PageProgress)
                else progressState.set(NewsFeedProgressState.NoProgress)
            }
        },
        true
    )

    init {
        loadCurrentUser()
    }

    private fun loadNextPage(page: Int): Single<List<ElementModelFeed>> {
        return interactor.loadNewsFeedPage(page,
                                           if (1 == page) null else (dataState.get() as? NewsFeedDataState.Data)?.newsFeed?.last()?.createdAtDateTime)
            .subscribeOn(rxSchedulersAbs.ioScheduler)
            .observeOn(rxSchedulersAbs.computationScheduler)
            .flatMap {
                Observable.fromIterable(it)
                    .map<ElementModelFeed> { it.toNewsFeedElement(currentUser.get()) }
                    .toList()
            }
            .onErrorReturn {
                Timber.d(it, "error during page $page load")
                listOf(
                    ElementModelFeed.Poll("${page * 20 + 0}",
                                          currentUser.get(),
                                          DateTime.now(),
                                          true,
                                          true,
                                          null,
                                          "Is it true?",
                                          listOf(PollChoice("1", "Yes", emptyList()), PollChoice("2", "No", listOf())).toPollVotedResult(),
                                          null,
                                          null,
                                          ElementLikes(currentUser.get().uid,
                                                                                                                                       emptyList()),
                                          ElementComments(emptyList())
                    ),
                    ElementModelFeed.Poll("${page * 20 + 1}",
                                          currentUser.get(),
                                          DateTime.now(),
                                          false,
                                          true,
                                          null,
                                          "Is it true?",
                                          listOf(PollChoice("1", "Yes", emptyList()),
                                                 PollChoice("2", "No", listOf(currentUser.get()))).toPollVotedResult(),
                                          null,
                                          DateTime(2018, 1, 5, 10, 23, 0),
                                          ElementLikes(currentUser.get().uid,
                                                                                                                                       emptyList()),
                                          ElementComments(emptyList())
                    ),
                    ElementModelFeed.Poll("${page * 20 + 2}",
                                          currentUser.get(),
                                          DateTime.now(),
                                          false,
                                          true,
                                          null,
                                          "Is it true?",
                                          listOf(PollChoice("1", "Yes", listOf(currentUser.get())),
                                                 PollChoice("2", "No", listOf(currentUser.get(), currentUser.get())))
                                              .toPollVotedResult(),
                                          PollChoice("1", "Yes", emptyList()),
                                          DateTime.now(),
                                          ElementLikes(currentUser.get().uid,
                                                                                                                                       emptyList()),
                                          ElementComments(emptyList())
                    ),
                    ElementModelFeed.Message("${page * 20 + 15}",
                                             currentUser.get(),
                                             DateTime.now(),
                                             false,
                                             true,
                                             null,
                                             "test message $page",
                                             ElementLikes(currentUser.get().uid,
                                                                                                                                          emptyList()),
                                             ElementComments(emptyList())
                    ),
                    ElementModelFeed.Message("${page * 20 + 16}",
                                             currentUser.get(),
                                             DateTime(2017, 12, 7, 15, 20),
                                             true,
                                             true,
                                             Announcement.SAFETY,
                                             "just an other message $page",
                                             ElementLikes(currentUser.get().uid,
                                                                                                                                          listOf(
                                                                                                                                              currentUser.get())),
                                             ElementComments(emptyList())
                    ),
                    ElementModelFeed.Message("${page * 20 + 17}",
                                             currentUser.get(),
                                             DateTime(2017, 12, 25, 15, 0),
                                             true,
                                             false,
                                             Announcement.GENERAL,
                                             "big text message. big text message. big text message. big text message. big text message. big text message. big text message. big text message. big text message. big text message. big text message. big text message. ",
                                             ElementLikes(currentUser.get().uid,
                                                                                                                                          listOf(
                                                                                                                                              currentUser.get(),
                                                                                                                                              currentUser.get(),
                                                                                                                                              currentUser.get(),
                                                                                                                                              currentUser.get(),
                                                                                                                                              currentUser.get(),
                                                                                                                                              currentUser.get())),
                                             ElementComments(emptyList())
                    ),
                    ElementModelFeed.Message("${page * 20 + 18}",
                                             currentUser.get(),
                                             DateTime(2017, 10, 7, 15, 0),
                                             false,
                                             false,
                                             Announcement.GOOD_NEWS,
                                             "",
                                             ElementLikes(currentUser.get().uid,
                                                                                                                                          emptyList()),
                                             ElementComments(listOf(
                                                                                         Comment(currentUser.get(),
                                                                                                 "just comment!!!",
                                                                                                 DateTime.now())))
                    ),
                    ElementModelFeed.Message("${page * 20 + 19}",
                                             currentUser.get(),
                                             DateTime(2017, 10, 1, 15, 0),
                                             false,
                                             false,
                                             Announcement.TRAGEDY,
                                             "test message",
                        /*"https://www.nytimes.com/2017/12/05/opinion/does-president-trump-want-to-negotiate-middle-east-peace.html?action=click&pgtype=Homepage&clickSource=story-heading&module=opinion-c-col-left-region&region=opinion-c-col-left-region&WT.nav=opinion-c-col-left-region"
                        , IconImage("https://static01.nyt.com/images/2017/12/06/opinion/06wed1/06wed1-superJumbo.jpg"),
                        "Does President Trump Want to Negotiate Middle East Peace?",
                        */
                                                                                     ElementLikes(currentUser.get().uid,
                                                                                                                                          emptyList()),
                                             ElementComments(listOf(
                                                                                         Comment(currentUser.get(),
                                                                                                 "comment 1!!!",
                                                                                                 DateTime.now()),
                                                                                         Comment(currentUser.get(),
                                                                                                 "comment 2!!!",
                                                                                                 DateTime(2017,
                                                                                                          12,
                                                                                                          7,
                                                                                                          20,
                                                                                                          40)),
                                                                                         Comment(currentUser.get(),
                                                                                                 "comment 3!!!",
                                                                                                 DateTime(2017,
                                                                                                          12,
                                                                                                          7,
                                                                                                          19,
                                                                                                          40)),
                                                                                         Comment(currentUser.get(),
                                                                                                 "comment 4!!!",
                                                                                                 DateTime(2017,
                                                                                                          12,
                                                                                                          6,
                                                                                                          23,
                                                                                                          40)),
                                                                                         Comment(currentUser.get(),
                                                                                                 "comment 5!!!",
                                                                                                 DateTime(2017,
                                                                                                          12,
                                                                                                          3,
                                                                                                          20,
                                                                                                          40))))
                    )
                )
            }
            .observeOn(rxSchedulersAbs.mainThreadScheduler)
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

    fun openAuthorOf(element: ElementModelFeed) {
        if (element is ElementModelFeed.Message) {
//            router.navigateTo(NavigateTo.PROFILE_DETAILS, element.author)
        }
    }

    fun changeUserLikeOf(element: ElementModelFeed) {
        // todo
    }

    fun openCommentsOf(element: ElementModelFeed) {
//        router.navigateTo(NavigateTo.ALL_COMMENTS_OF, element)
    }

    fun selectPollChoice(element: ElementModelFeed.Poll, choice: ElementModelFeed.Poll.PollChoiceResult) {
        val currentDataState = dataState.get()
        if (currentDataState is NewsFeedDataState.Data) {
            if (currentDataState.newsFeed.contains(element)) {
                val choices = element.choices.map {
                    if (it == choice) {
                        val voters = it.pollChoice.voters.toMutableList()
                        voters.add(currentUser.get())
                        it.copy(pollChoice = it.pollChoice.copy(voters = voters),
                                totalVotes = it.totalVotes + 1)
                    } else
                        it.copy(totalVotes = it.totalVotes + 1)
                }
                val newFeedElement = element.copy(choices = choices, userChoice = choice.pollChoice)

                currentDataState.newsFeed.replaceElement(element, newFeedElement)

                interactor.answerForPoll(choice.pollChoice, element.uid)
                    .compose(rxSchedulersAbs.getIOToMainTransformerSingle())
                    .subscribe({ updatedElement ->
                                   val upToDateDataState = dataState.get()
                                   if (upToDateDataState is NewsFeedDataState.Data) {
                                       if (upToDateDataState.newsFeed.contains(newFeedElement)) {
                                           upToDateDataState.newsFeed.replaceElement(newFeedElement,
                                                                                     updatedElement.toNewsFeedElement(currentUser.get()))
                                       }
                                   }
                               },
                               { t ->
                                   handleError(t)

                                   val upToDateDataState = dataState.get()
                                   if (upToDateDataState is NewsFeedDataState.Data) {
                                       if (upToDateDataState.newsFeed.contains(newFeedElement)) {
                                           upToDateDataState.newsFeed.replaceElement(newFeedElement, element)
                                           errorState.set(NewsFeedErrorState.NonFatalErrorState(t))
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

    private fun loadCurrentUser() {
        interactor.loadCurrentUser()
            .compose(rxSchedulersAbs.getIOToMainTransformer())
            .subscribe {
                currentUser.set(it)
                paginator.activate()
                observeAudienceChange()
            }
            .addTo(disposables)
    }

    private fun observeAudienceChange() {
        router.setResultListener(ResultCodes.AUDIENCE_SELECTION, {
            if (it is Audience) {
                selectedAudience.set(it)
            }
        })
        router.setResultListener(ResultCodes.NEWS_FEED_ITEM_CREATED, {
            if (it is NewsFeedItem) {
                // todo may be should add it here and do not do a full refresh?
                paginator.forceRefresh()
            }
        })

        selectedAudience
            .toObservable()
            .distinctUntilChanged()
            .observeOn(rxSchedulersAbs.mainThreadScheduler)
            .subscribe { paginator.forceRefresh() }
            .addTo(disposables)
    }

    override fun onCleared() {
        paginator.release()
        router.removeResultListener(ResultCodes.AUDIENCE_SELECTION)

        super.onCleared()
    }
}

private fun NewsFeedItem.toNewsFeedElement(currentUser: User): ElementModelFeed =
    when (this) {
        is NewsFeedItemPoll -> ElementModelFeed.Poll(uid,
                                                     user,
                                                     pollCreatedAt,
                                                     locked,
                                                     pinned,
                                                     announcement,
                                                     questionText,
                                                     choices.toPollVotedResult(),
                                                     choices.firstOrNull { null != it.voters.firstOrNull { it.uid == currentUser.uid } },
                                                     pollEndsTime.endsFrom(pollCreatedAt),
                                                     ElementLikes(
                                                                                                 currentUser.uid,
                                                                                                 likes),
                                                     ElementComments(
                                                                                                 comments))
        else                -> throw IllegalArgumentException("unknown NewsFeedItem: $this")
    }

private fun List<PollChoice>.toPollVotedResult(): List<ElementModelFeed.Poll.PollChoiceResult> {
    val totalVotes = fold(0, { acc, pollChoice -> acc + pollChoice.voters.size })
    val mostVoted = fold(ArrayList(), { acc: MutableList<PollChoice>, pollChoice ->
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
    return map { ElementModelFeed.Poll.PollChoiceResult(it, totalVotes, mostVoted.contains(it)) }
}