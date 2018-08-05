package io.scal.ambi.ui.profile.details

import android.content.Context
import android.databinding.ObservableField
import com.ambi.work.R
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.entity.feed.FeedType
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.binding.observable.OptimizedObservableArrayList
import io.scal.ambi.extensions.binding.replaceElement
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.interactor.profile.IProfileDetailsInteractor
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.base.viewmodel.BaseUserViewModel
import io.scal.ambi.ui.global.base.viewmodel.toGoodUserMessage
import io.scal.ambi.ui.global.model.DynamicUserChoicer
import io.scal.ambi.ui.global.model.PaginatorStateViewController
import io.scal.ambi.ui.global.model.createPaginator
import io.scal.ambi.ui.global.picker.FileResource
import io.scal.ambi.ui.home.newsfeed.list.NewsFeedViewModelActions
import io.scal.ambi.ui.home.newsfeed.list.adapter.INewsFeedViewModel
import io.scal.ambi.ui.home.newsfeed.list.changeLikes
import io.scal.ambi.ui.home.newsfeed.list.data.UIModelFeed
import io.scal.ambi.ui.home.newsfeed.list.setupLike
import io.scal.ambi.ui.home.newsfeed.list.toNewsFeedElement
import javax.inject.Inject

class ProfileDetailsViewModel @Inject internal constructor(private val context: Context,
                                                           router: BetterRouter,
                                                           val profileUid: String,
                                                           val searchViewModel: ProfileSearchViewModel,
                                                           private val interactor: IProfileDetailsInteractor,
                                                           rxSchedulersAbs: RxSchedulersAbs) :
    BaseUserViewModel(router, { interactor.loadCurrentUser() }, rxSchedulersAbs),
    INewsFeedViewModel {

    private lateinit var profileUidToShow: String
    var profileUidToShowIsCurrent: Boolean = false
    var entityType = FeedType.PERSONAL.feedType;

    internal val progressState = ObservableField<ProfileDetailsProgressState>(ProfileDetailsProgressState.TotalProgress)
    internal val errorState = ObservableField<ProfileDetailsErrorState>()
    val dataState = ObservableField<ProfileDetailsDataState>()

    private val paginator = createPaginator(
        { page -> executeLoadNextPage(page) },
        object :
            PaginatorStateViewController<UIModelFeed, ProfileDetailsProgressState, ProfileDetailsErrorState>(context, progressState, errorState) {

            override fun generateProgressEmptyState() = ProfileDetailsProgressState.EmptyProgress
            override fun generateProgressNoState() = ProfileDetailsProgressState.NoProgress
            override fun generateProgressRefreshState() = ProfileDetailsProgressState.RefreshProgress
            override fun generateProgressPageState() = ProfileDetailsProgressState.PageProgress

            override fun generateErrorFatal(message: String) = ProfileDetailsErrorState.FatalErrorState(message)
            override fun generateErrorNonFatal(message: String) = ProfileDetailsErrorState.NonFatalErrorState(message)
            override fun generateErrorNo() = ProfileDetailsErrorState.NoErrorState

            override fun showEmptyView(show: Boolean) {
                if (show) dataState.set(ProfileDetailsDataState.DataNewsFeedEmpty(dataState.get()?.profileInfo))
            }

            override fun showData(show: Boolean, data: List<UIModelFeed>) {
                if (show) {
                    dataState.set(ProfileDetailsDataState.DataNewsFeed(dataState.get()?.profileInfo, OptimizedObservableArrayList(data)))
                }
            }
        },
        true, true
    )

    private val userActions = NewsFeedViewModelActions(router,
                                                       { uiModelFeed, action ->
                                                           interactor
                                                               .changeUserLikeForPost(uiModelFeed.feedItem, action == DynamicUserChoicer.Action.LIKE)
                                                               .compose(rxSchedulersAbs.ioToMainTransformerCompletable)
                                                       },
                                                       { uiModelFeed, commentTet ->
                                                           interactor.sendUserCommentToPost(uiModelFeed.feedItem, commentTet)
                                                               .doOnError {
                                                                   handleError(it)

                                                                   errorState.set(ProfileDetailsErrorState.NonFatalErrorState(
                                                                       it.toGoodUserMessage(context))
                                                                   )
                                                                   errorState.set(ProfileDetailsErrorState.NoErrorState)
                                                               }
                                                       },
                                                       { uiModelFeed, pollChoiceResult ->
                                                           interactor.answerForPoll(uiModelFeed.feedItem, pollChoiceResult.pollChoice)
                                                               .doOnError {
                                                                   handleError(it)

                                                                   errorState.set(ProfileDetailsErrorState.NonFatalErrorState(
                                                                       it.toGoodUserMessage(context))
                                                                   )
                                                                   errorState.set(ProfileDetailsErrorState.NoErrorState)
                                                               }
                                                       },
                                                       {
                                                           val currentState = dataState.get()
                                                           if (currentState is ProfileDetailsDataState.DataNewsFeed) {
                                                               currentState.newsFeed
                                                           } else {
                                                               null
                                                           }
                                                       },
                                                       rxSchedulersAbs)

    override fun onCurrentUserFetched(user: User) {
        super.onCurrentUserFetched(user)

        profileUidToShow = if (profileUid.isEmpty()) user.uid else profileUid
        profileUidToShowIsCurrent = profileUidToShow == user.uid

        if (profileUidToShowIsCurrent) {
            onInit(user)
        } else {
            interactor.loadUser(profileUidToShow)
                .compose(rxSchedulersAbs.getIOToMainTransformer())
                .subscribe({ onInit(it) },
                           { t ->
                               handleError(t)

                               errorState.set(ProfileDetailsErrorState.FatalErrorState(t.toGoodUserMessage(context)))
                           })
                .addTo(disposables)
        }
    }

    override fun openAuthorOf(element: UIModelFeed) {
        userActions.openAuthorOf(element)
    }

    override fun openCommentsOf(element: UIModelFeed) {
        userActions.openCommentsOf(element)
    }

    override fun changeUserLikeOf(element: UIModelFeed) {
        val currentDataState = dataState.get()
        if (currentDataState is ProfileDetailsDataState.DataNewsFeed) {
            userActions.changeUserLikeOf(currentDataState.newsFeed, element, currentUser.get())
        }
    }

    override fun sendCommentForElement(element: UIModelFeed) {
        userActions.sendCommentForElement(element)
    }

    override fun selectPollChoice(element: UIModelFeed.Poll, choice: UIModelFeed.Poll.PollChoiceResult) {
        val currentDataState = dataState.get()
        if (currentDataState is ProfileDetailsDataState.DataNewsFeed) {
            userActions.selectPollChoice(currentDataState.newsFeed, element, choice, currentUser.get())
        }
    }

    fun retry() {
        if (null == dataState.get()?.profileInfo) {
            onCurrentUserFetched(currentUser.get())
        } else {
            refresh()
        }
    }

    fun refresh() {
        paginator.refresh()
    }

    fun loadNextPage() {
        paginator.loadNewPage()
    }

    fun openProfile() {
        router.navigateTo(NavigateTo.PROFILE_DETAILS)
    }

    fun openResumeEdit() {
        router.navigateTo(NavigateTo.PROFILE_RESUME)
    }

    fun openSettings() {
        router.navigateTo(NavigateTo.PROFILE_PASSWORD_CHANGE)
    }

    fun attachAvatar(fileResource: FileResource) {
        attachNewImage(fileResource, { interactor.attachAvatarImage(it) })
    }

    fun attachBanner(fileResource: FileResource) {
        attachNewImage(fileResource, { interactor.attachBannerImage(it) })
    }

    private fun attachNewImage(fileResource: FileResource, function: (FileResource) -> Single<User>) {
        if (profileUidToShowIsCurrent) {
            function.invoke(fileResource)
                .compose(rxSchedulersAbs.getIOToMainTransformerSingle())
                .doOnDispose { fileResource.cleanUp() }
                .subscribe({
                               currentUser.set(it)
                               dataState.set(dataState.get().copyProfileInfo(it.toUIProfile(context, profileUidToShowIsCurrent)))
                           },
                           {
                               handleError(it)

                               errorState.set(ProfileDetailsErrorState.NonFatalErrorState(
                                   it.toGoodUserMessage(context))
                               )
                               errorState.set(ProfileDetailsErrorState.NoErrorState)
                           })
                .addTo(disposables)
        }
    }

    private fun executeLoadNextPage(page: Int): Single<List<UIModelFeed>> {
        return interactor
            .loadNewsFeedPage(entityType,profileUidToShowIsCurrent, profileUidToShow, page)
            .subscribeOn(rxSchedulersAbs.ioScheduler)
            .observeOn(rxSchedulersAbs.computationScheduler)
            .flatMap {
                Observable.fromIterable(it)
                    .map<UIModelFeed> { it.toNewsFeedElement(currentUser.get()) }
                    .toList()
            }
            .observeOn(rxSchedulersAbs.mainThreadScheduler)
    }

    private fun onInit(user: User) {
        dataState.set(ProfileDetailsDataState.DataInfoOnly(user.toUIProfile(context, profileUidToShowIsCurrent)))

        paginator.activate()

        paginator.refresh()

        userActions
            .observeLikes()
            .observeOn(rxSchedulersAbs.mainThreadScheduler)
            .subscribe {
                val currentDataState = dataState.get()
                if (currentDataState is ProfileDetailsDataState.DataNewsFeed) {
                    val element = currentDataState.newsFeed.firstOrNull { item -> item.uid == it.first.uid }
                    if (null != element) {
                        val newLikes = element.likes.setupLike(currentUser.get(), it.second == DynamicUserChoicer.Action.LIKE)
                        currentDataState.newsFeed.replaceElement(element, element.changeLikes(newLikes))
                    }
                }
            }
            .addTo(disposables)
    }
}

private fun User.toUIProfile(context: Context, showSettings: Boolean): UIProfile {
    val workAt = workExperience?.lastOrNull()
    return UIProfile(name,
                     avatar,
                     banner,
                     workAt?.let { "${it.title} at ${it.description}" },
                     liveAt?.let { context.getString(R.string.profile_details_life_at, it) },
                     showSettings
    )
}
