package io.scal.ambi.ui.home.newsfeed.list

import io.scal.ambi.entity.chat.ChatAttachment
import io.scal.ambi.entity.feed.*
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.binding.observable.ObservableString
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.ui.home.newsfeed.list.data.UIComments
import io.scal.ambi.ui.home.newsfeed.list.data.UILikes
import io.scal.ambi.ui.home.newsfeed.list.data.UIModelFeed

fun NewsFeedItem.toNewsFeedElement(currentUser: User): UIModelFeed =
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
                                                        choices.firstOrNull { null != it.voters.firstOrNull { it == currentUser.uid } },
                                                        pollEndsTime,
                                                        UILikes(currentUser, likes),
                                                        UIComments(comments),
                                                        ObservableString())
        is NewsFeedItemUpdate       -> UIModelFeed.Message(uid,
                                                           this,
                                                           user,
                                                           messageCreatedAt,
                                                           locked,
                                                           pinned,
                                                           null,
                                                           messageText,
                                                           UILikes(currentUser, likes),
                                                           UIComments(comments),
                                                           ObservableString(),
                                                           attachments
                                                               .firstOrNull { it is ChatAttachment.LocalImage || it is ChatAttachment.Image }
                                                               ?.pathAsString
                                                               ?.let { IconImage(it) }
        )
        is NewsFeedItemAnnouncement -> UIModelFeed.Message(uid,
                                                           this,
                                                           user,
                                                           messageCreatedAt,
                                                           locked,
                                                           pinned,
                                                           announcementType,
                                                           messageText,
                                                           UILikes(currentUser, likes),
                                                           UIComments(comments),
                                                           ObservableString(),
                                                           attachments
                                                               .firstOrNull { it is ChatAttachment.LocalImage || it is ChatAttachment.Image }
                                                               ?.pathAsString
                                                               ?.let { IconImage(it) }
        )
        else                        -> throw IllegalArgumentException("unknown NewsFeedItem: $this")
    }

fun List<PollChoice>.toPollVotedResult(): List<UIModelFeed.Poll.PollChoiceResult> {
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

fun UILikes.setupLike(currentUser: User, like: Boolean): UILikes =
    if (like) {
        UILikes(currentUser, allUsersLiked.filter { it.uid != currentUser.uid }.plus(currentUser))
    } else {
        UILikes(currentUser, allUsersLiked.filter { it.uid != currentUser.uid })
    }

@Suppress("REDUNDANT_ELSE_IN_WHEN")
fun UIModelFeed.changeLikes(newLikes: UILikes): UIModelFeed =
    when (this) {
        is UIModelFeed.Message -> copy(likes = newLikes)
        is UIModelFeed.Poll    -> copy(likes = newLikes)
        is UIModelFeed.Link    -> copy(likes = newLikes)
        else                   -> throw IllegalStateException("unknown element")
    }