package io.scal.ambi.ui.home.newsfeed

import android.databinding.ObservableField
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.R
import io.scal.ambi.entity.User
import io.scal.ambi.entity.actions.Comment
import io.scal.ambi.entity.actions.ElementComments
import io.scal.ambi.entity.actions.ElementLikes
import io.scal.ambi.extensions.binding.binders.toFrescoImagePath
import io.scal.ambi.extensions.binding.observable.OptimizedObservableArrayList
import io.scal.ambi.extensions.binding.replaceElements
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.IconImageUser
import io.scal.ambi.model.interactor.home.newsfeed.INewsFeedInteractor
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import io.scal.ambi.ui.global.model.Paginator
import org.joda.time.LocalDateTime
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class NewsFeedViewModel @Inject constructor(router: Router,
                                            private val interactor: INewsFeedInteractor,
                                            private val rxSchedulersAbs: RxSchedulersAbs) : BaseViewModel(router) {

    internal val progressState = ObservableField<NewsFeedProgressState>()
    internal val errorState = ObservableField<NewsFeedErrorState>()
    internal val dataState = ObservableField<NewsFeedDataState>()

    val currentUser = ObservableField<User>()

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
        refresh()
        loadCurrentUser()
    }

    private fun loadNextPage(page: Int): Single<List<ModelFeedElement>> =
        interactor.loadNewsFeedPage(page)
            .subscribeOn(rxSchedulersAbs.ioScheduler)
            .observeOn(rxSchedulersAbs.computationScheduler)
            .flatMap {
                Observable.fromIterable(it)
                    .map<ModelFeedElement>({ throw NotImplementedError("implement model change") })
                    .toList()
            }
            .onErrorReturnItem(
                listOf(
                    ModelFeedElement.Message("${page * 5 + 0}",
                                             "mig35",
                                             IconImage(R.drawable.ic_profile),
                                             LocalDateTime.now(),
                                             "test message $page",
                                             ElementLikes(false, emptyList()),
                                             ElementComments(emptyList())
                    ),
                    ModelFeedElement.Message("${page * 5 + 1}",
                                             "mig35",
                                             IconImage("https://scontent-arn2-1.xx.fbcdn.net/v/t1.0-9/12140762_1159067420823544_4471328164031495581_n.jpg?oh=e9255c0340cca64e7f51bb114bc25f9c&oe=5AC9097D"),
                                             LocalDateTime(2017, 12, 7, 15, 20),
                                             "just an other message $page",
                                             ElementLikes(true, listOf(User("0", IconImageUser(R.drawable.ic_profile.toFrescoImagePath())))),
                                             ElementComments(emptyList())
                    ),
                    ModelFeedElement.Message("${page * 5 + 2}",
                                             "mig35",
                                             IconImage("https://cdn.pixabay.com/photo/2013/04/06/11/50/image-editing-101040_960_720.jpg"),
                                             LocalDateTime(2017, 12, 25, 15, 0),
                                             "big text message. big text message. big text message. big text message. big text message. big text message. big text message. big text message. big text message. big text message. big text message. big text message. ",
                                             ElementLikes(false, listOf(User(), User(), User(), User(), User(), User())),
                                             ElementComments(emptyList())
                    ),
                    ModelFeedElement.Message("${page * 5 + 3}",
                                             "mig35 2",
                                             IconImage("https://cloud.netlifyusercontent.com/assets/344dbf88-fdf9-42bb-adb4-46f01eedd629/68dd54ca-60cf-4ef7-898b-26d7cbe48ec7/10-dithering-opt.jpg"),
                                             LocalDateTime(2017, 10, 7, 15, 0),
                                             "",
                                             ElementLikes(false, emptyList()),
                                             ElementComments(listOf(Comment(User(), "just comment!!!", LocalDateTime.now())))
                    ),
                    ModelFeedElement.Message("${page * 5 + 4}",
                                             "New Your Times",
                                             IconImage("https://cloud.netlifyusercontent.com/assets/344dbf88-fdf9-42bb-adb4-46f01eedd629/68dd54ca-60cf-4ef7-898b-26d7cbe48ec7/10-dithering-opt.jpg"),
                                             LocalDateTime(2017, 10, 1, 15, 0),
                                             "test message",
                        /*"https://www.nytimes.com/2017/12/05/opinion/does-president-trump-want-to-negotiate-middle-east-peace.html?action=click&pgtype=Homepage&clickSource=story-heading&module=opinion-c-col-left-region&region=opinion-c-col-left-region&WT.nav=opinion-c-col-left-region"
                        , IconImage("https://static01.nyt.com/images/2017/12/06/opinion/06wed1/06wed1-superJumbo.jpg"),
                        "Does President Trump Want to Negotiate Middle East Peace?",
                        */
                                             ElementLikes(false, emptyList()),
                                             ElementComments(listOf(Comment(User(), "comment 1!!!", LocalDateTime.now()),
                                                                    Comment(User(), "comment 2!!!", LocalDateTime(2017, 12, 7, 20, 40)),
                                                                    Comment(User(), "comment 3!!!", LocalDateTime(2017, 12, 7, 19, 40)),
                                                                    Comment(User(), "comment 4!!!", LocalDateTime(2017, 12, 6, 23, 40)),
                                                                    Comment(User(), "comment 5!!!", LocalDateTime(2017, 12, 3, 20, 40))))
                    )
                ))
            .observeOn(rxSchedulersAbs.mainThreadScheduler)

    fun changeAudience() {

    }

    fun createAnnouncement() {
        router.navigateTo(NavigateTo.CREATE_ANNOUNCEMENT)
    }

    fun createPoll() {
        router.navigateTo(NavigateTo.CREATE_POLL)
    }

    fun refresh() {
        paginator.refresh()
    }

    private fun loadCurrentUser() {
        interactor.loadCurrentUser()
            .compose(rxSchedulersAbs.getIOToMainTransformer())
            .subscribe { currentUser.set(it) }
            .addTo(disposables)
    }

    override fun onCleared() {
        paginator.release()

        super.onCleared()
    }
}