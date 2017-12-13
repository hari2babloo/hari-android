package io.scal.ambi.ui.home.newsfeed.list

import android.databinding.ObservableField
import android.os.SystemClock
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.scal.ambi.entity.User
import io.scal.ambi.entity.actions.Comment
import io.scal.ambi.entity.actions.ElementComments
import io.scal.ambi.entity.actions.ElementLikes
import io.scal.ambi.entity.feed.Announcement
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.entity.feed.NewsFeedItem
import io.scal.ambi.entity.feed.PollChoice
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
import org.joda.time.LocalDateTime
import ru.terrakok.cicerone.Router
import java.util.concurrent.TimeUnit
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
        object : Paginator.ViewController<ModelFeedElement> {
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

            override fun showData(show: Boolean, data: List<ModelFeedElement>) {
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
        }
    )

    init {
        loadCurrentUser()
        observeAudienceChange()
    }

    private fun loadNextPage(page: Int): Single<List<ModelFeedElement>> {
        return Completable.fromAction {
            while (null == currentUser.get()) {
                SystemClock.sleep(100)
            }
        }
            .subscribeOn(Schedulers.computation())
            .andThen(
                interactor.loadNewsFeedPage(page, (dataState.get() as? NewsFeedDataState.Data)?.newsFeed?.last()?.dateTime)
                    .subscribeOn(rxSchedulersAbs.ioScheduler)
                    .observeOn(rxSchedulersAbs.computationScheduler)
                    .flatMap {
                        Observable.fromIterable(it)
                            .map<ModelFeedElement> { it.toNewsFeedElement() }
                            .toList()
                    }
                    .onErrorReturn {
                        listOf(
                            ModelFeedElement.Poll("${page * 20 + 0}",
                                                  currentUser.get(),
                                                  LocalDateTime.now(),
                                                  true,
                                                  true,
                                                  null,
                                                  "Is it true?",
                                                  listOf(PollChoice("1", "Yes", emptyList()), PollChoice("2", "No", listOf())).toPollVotedResult(),
                                                  null,
                                                  null,
                                                  ElementLikes(false, emptyList()),
                                                  ElementComments(emptyList())
                            ),
                            ModelFeedElement.Poll("${page * 20 + 1}",
                                                  currentUser.get(),
                                                  LocalDateTime.now(),
                                                  false,
                                                  true,
                                                  null,
                                                  "Is it true?",
                                                  listOf(PollChoice("1", "Yes", emptyList()),
                                                         PollChoice("2", "No", listOf(currentUser.get()))).toPollVotedResult(),
                                                  null,
                                                  LocalDateTime(2018, 1, 5, 10, 23, 0),
                                                  ElementLikes(false, emptyList()),
                                                  ElementComments(emptyList())
                            ),
                            ModelFeedElement.Poll("${page * 20 + 2}",
                                                  currentUser.get(),
                                                  LocalDateTime.now(),
                                                  false,
                                                  true,
                                                  null,
                                                  "Is it true?",
                                                  listOf(PollChoice("1", "Yes", listOf(currentUser.get())),
                                                         PollChoice("2", "No", listOf(currentUser.get(), currentUser.get())))
                                                      .toPollVotedResult(),
                                                  PollChoice("1", "Yes", emptyList()),
                                                  LocalDateTime.now(),
                                                  ElementLikes(false, emptyList()),
                                                  ElementComments(emptyList())
                            ),
                            ModelFeedElement.Message("${page * 20 + 15}",
                                                     currentUser.get(),
                                                     LocalDateTime.now(),
                                                     false,
                                                     true,
                                                     null,
                                                     "test message $page",
                                                     ElementLikes(false, emptyList()),
                                                     ElementComments(emptyList())
                            ),
                            ModelFeedElement.Message("${page * 20 + 16}",
                                                     currentUser.get(),
                                                     LocalDateTime(2017, 12, 7, 15, 20),
                                                     true,
                                                     true,
                                                     Announcement.SAFETY,
                                                     "just an other message $page",
                                                     ElementLikes(true, listOf(currentUser.get())),
                                                     ElementComments(emptyList())
                            ),
                            ModelFeedElement.Message("${page * 20 + 17}",
                                                     currentUser.get(),
                                                     LocalDateTime(2017, 12, 25, 15, 0),
                                                     true,
                                                     false,
                                                     Announcement.GENERAL,
                                                     "big text message. big text message. big text message. big text message. big text message. big text message. big text message. big text message. big text message. big text message. big text message. big text message. ",
                                                     ElementLikes(false,
                                                                  listOf(currentUser.get(),
                                                                         currentUser.get(),
                                                                         currentUser.get(),
                                                                         currentUser.get(),
                                                                         currentUser.get(),
                                                                         currentUser.get())),
                                                     ElementComments(emptyList())
                            ),
                            ModelFeedElement.Message("${page * 20 + 18}",
                                                     currentUser.get(),
                                                     LocalDateTime(2017, 10, 7, 15, 0),
                                                     false,
                                                     false,
                                                     Announcement.GOOD_NEWS,
                                                     "",
                                                     ElementLikes(false, emptyList()),
                                                     ElementComments(listOf(Comment(currentUser.get(),
                                                                                    "just comment!!!",
                                                                                    LocalDateTime.now())))
                            ),
                            ModelFeedElement.Message("${page * 20 + 19}",
                                                     currentUser.get(),
                                                     LocalDateTime(2017, 10, 1, 15, 0),
                                                     false,
                                                     false,
                                                     Announcement.TRAGEDY,
                                                     "test message",
                                /*"https://www.nytimes.com/2017/12/05/opinion/does-president-trump-want-to-negotiate-middle-east-peace.html?action=click&pgtype=Homepage&clickSource=story-heading&module=opinion-c-col-left-region&region=opinion-c-col-left-region&WT.nav=opinion-c-col-left-region"
                                , IconImage("https://static01.nyt.com/images/2017/12/06/opinion/06wed1/06wed1-superJumbo.jpg"),
                                "Does President Trump Want to Negotiate Middle East Peace?",
                                */
                                                     ElementLikes(false, emptyList()),
                                                     ElementComments(listOf(Comment(currentUser.get(),
                                                                                    "comment 1!!!",
                                                                                    LocalDateTime.now()),
                                                                            Comment(currentUser.get(),
                                                                                    "comment 2!!!",
                                                                                    LocalDateTime(2017,
                                                                                                  12,
                                                                                                  7,
                                                                                                  20,
                                                                                                  40)),
                                                                            Comment(currentUser.get(),
                                                                                    "comment 3!!!",
                                                                                    LocalDateTime(2017,
                                                                                                  12,
                                                                                                  7,
                                                                                                  19,
                                                                                                  40)),
                                                                            Comment(currentUser.get(),
                                                                                    "comment 4!!!",
                                                                                    LocalDateTime(2017,
                                                                                                  12,
                                                                                                  6,
                                                                                                  23,
                                                                                                  40)),
                                                                            Comment(currentUser.get(),
                                                                                    "comment 5!!!",
                                                                                    LocalDateTime(2017,
                                                                                                  12,
                                                                                                  3,
                                                                                                  20,
                                                                                                  40))))
                            )
                        )
                    }
                    .delay(5, TimeUnit.SECONDS)
                    .observeOn(rxSchedulersAbs.mainThreadScheduler))
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

    fun openAuthorOf(element: ModelFeedElement) {
        if (element is ModelFeedElement.Message) {
//            router.navigateTo(NavigateTo.PROFILE_DETAILS, element.author)
        }
    }

    fun changeUserLikeOf(element: ModelFeedElement) {
        // todo
    }

    fun openCommentsOf(element: ModelFeedElement) {
//        router.navigateTo(NavigateTo.ALL_COMMENTS_OF, element)
    }

    fun selectPollChoice(element: ModelFeedElement.Poll, choice: ModelFeedElement.Poll.PollChoiceResult) {
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
                                           upToDateDataState.newsFeed.replaceElement(newFeedElement, updatedElement.toNewsFeedElement())
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
            .subscribe { currentUser.set(it) }
            .addTo(disposables)
    }

    private fun observeAudienceChange() {
        router.setResultListener(ResultCodes.AUDIENCE_SELECTION, {
            if (it is Audience) {
                selectedAudience.set(it)
            }
        })
        router.setResultListener(ResultCodes.NEWS_FEED_ITEM_CREATED, {
            paginator.refreshForce()
        })

        selectedAudience
            .toObservable()
            .distinctUntilChanged()
            .observeOn(rxSchedulersAbs.mainThreadScheduler)
            .subscribe { paginator.refreshForce() }
            .addTo(disposables)
    }

    override fun onCleared() {
        paginator.release()
        router.removeResultListener(ResultCodes.AUDIENCE_SELECTION)

        super.onCleared()
    }
}

private fun NewsFeedItem.toNewsFeedElement(): ModelFeedElement? {
    throw NotImplementedError("implement model change")
}

private fun List<PollChoice>.toPollVotedResult(): List<ModelFeedElement.Poll.PollChoiceResult> {
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
    return map { ModelFeedElement.Poll.PollChoiceResult(it, totalVotes, mostVoted.contains(it)) }
}