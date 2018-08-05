package io.scal.ambi.ui.home.newsfeed.list

import android.databinding.ObservableList
import io.reactivex.Completable
import io.reactivex.Single
import io.scal.ambi.entity.actions.Comment
import io.scal.ambi.entity.feed.NewsFeedItem
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.binding.replaceElement
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.model.DynamicUserChoicer
import io.scal.ambi.ui.home.newsfeed.list.data.UIComments
import io.scal.ambi.ui.home.newsfeed.list.data.UIModelFeed

class NewsFeedViewModelActions(private val router: BetterRouter,
                               private val likeAction: (UIModelFeed, DynamicUserChoicer.Action) -> Completable,
                               private val commentAction: (UIModelFeed, String) -> Single<Comment>,
                               private val pollChoiceAction: (UIModelFeed.Poll, UIModelFeed.Poll.PollChoiceResult) -> Single<NewsFeedItem>,
                               private val getNewsFeed: () -> ObservableList<UIModelFeed>?,
                               private val rxSchedulersAbs: RxSchedulersAbs) {

    private val userLikeChoicer = DynamicUserChoicer<UIModelFeed>(rxSchedulersAbs,
            { uiModelFeed, action ->
                likeAction.invoke(uiModelFeed, action)
                        .compose(rxSchedulersAbs.ioToMainTransformerCompletable)
            },
            { uiModelFeed -> uiModelFeed.uid })


    fun openAuthorOf(element: UIModelFeed) {
        val userId =
                when (element) {
                    is UIModelFeed.Message -> element.author.uid
                    is UIModelFeed.Poll    -> element.author.uid
                    is UIModelFeed.Link    -> element.actor.uid
                }
        router.navigateTo(NavigateTo.PROFILE_DETAILS, userId)
    }

    fun openCommentsOf(element: UIModelFeed) {
        //        router.navigateTo(NavigateTo.ALL_COMMENTS_OF, element)
    }

    fun changeUserLikeOf(newsFeed: ObservableList<UIModelFeed>, element: UIModelFeed, currentUser: User) {
        if (newsFeed.contains(element)) {
            val newLikes =
                    if (element.likes.currentUserLiked) {
                        userLikeChoicer.changeUserChoice(element, DynamicUserChoicer.Action.NONE, DynamicUserChoicer.Action.LIKE)
                        element.likes.setupLike(currentUser, false)
                    } else {
                        userLikeChoicer.changeUserChoice(element, DynamicUserChoicer.Action.LIKE, DynamicUserChoicer.Action.NONE)
                        element.likes.setupLike(currentUser, true)
                    }

            newsFeed.replaceElement(element, element.changeLikes(newLikes))
        }
    }

    fun sendCommentForElement(element: UIModelFeed) {
        val userCommentText = element.userCommentText.data.get().orEmpty().trim()
        if (userCommentText.isNotEmpty()) {
            element.userCommentText.enabled.set(false)

            commentAction.invoke(element, userCommentText)
                    .compose(rxSchedulersAbs.getIOToMainTransformerSingle())
                    .subscribe({
                        val newsFeed = getNewsFeed.invoke()
                        if (null != newsFeed) {
                            val listElement = newsFeed.firstOrNull { item -> item.uid == element.uid }

                            if (null != listElement) {
                                val newComments = listElement.comments.comments.toMutableList()
                                newComments.add(0, it)
                                val updatedElement = listElement.updateComments(UIComments(newComments))

                                newsFeed.replaceElement(listElement, updatedElement)

                                listElement.userCommentText.enabled.set(true)
                                listElement.userCommentText.data.set("")
                            }
                        }
                    },
                            { element.userCommentText.enabled.set(true) })
        }
    }

    fun selectPollChoice(newsFeed: ObservableList<UIModelFeed>,
                         element: UIModelFeed.Poll,
                         choice: UIModelFeed.Poll.PollChoiceResult,
                         currentUser: User) {
        if (newsFeed.contains(element)) {
            val newPollChoices = element
                    .choices
                    .map { it.pollChoice }
                    .map { if (it.uid == choice.pollChoice.uid) choice.pollChoice.copy(voters = choice.pollChoice.voters.plus(currentUser.uid)) else it }

            newsFeed
                    .replaceElement(element, element.copy(choices = newPollChoices.toPollVotedResult(), userChoice = choice.pollChoice))

            pollChoiceAction.invoke(element, choice)
                    .compose(rxSchedulersAbs.getIOToMainTransformerSingle())
                    .subscribe({ updatedElement ->
                        val upToDateNewsFeed = getNewsFeed.invoke()
                        if (null != upToDateNewsFeed) {
                            val listElement = upToDateNewsFeed.firstOrNull { item -> item.uid == element.uid }

                            if (null != listElement) {
                                upToDateNewsFeed.replaceElement(listElement, updatedElement.toNewsFeedElement(currentUser))
                            }
                        }
                    },
                            { t ->
                                val upToDateNewsFeed = getNewsFeed.invoke()
                                if (null != upToDateNewsFeed) {
                                    val listElement = upToDateNewsFeed.firstOrNull { item -> item.uid == element.uid } as? UIModelFeed.Poll

                                    if (null != listElement) {
                                        val oldPollChoices = element.choices.map { it.pollChoice }.toPollVotedResult()

                                        upToDateNewsFeed.replaceElement(listElement,
                                                listElement.copy(choices = oldPollChoices, userChoice = null))
                                    }
                                }
                            })
        }
    }

    fun observeLikes() = userLikeChoicer.activate()
}