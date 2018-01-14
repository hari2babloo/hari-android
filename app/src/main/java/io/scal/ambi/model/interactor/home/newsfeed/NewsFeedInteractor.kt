package io.scal.ambi.model.interactor.home.newsfeed

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.actions.Comment
import io.scal.ambi.entity.feed.*
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.view.IconImageUser
import io.scal.ambi.model.repository.data.newsfeed.IPostsRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import org.joda.time.DateTime
import timber.log.Timber
import javax.inject.Inject

class NewsFeedInteractor @Inject constructor(private val postsRepository: IPostsRepository,
                                             private val localUserDataRepository: ILocalUserDataRepository) : INewsFeedInteractor {

    override fun loadCurrentUser(): Observable<User> =
        localUserDataRepository.observeCurrentUser()

    override fun loadNewsFeedPage(page: Int, dateTime: DateTime?): Single<List<NewsFeedItem>> =
        postsRepository.loadPostsGeneral(dateTime?.millis)
            .onErrorReturn {
                Timber.d(it, "error during page $page load")
                generateTestData(User.asSimple("wef",
                                               IconImageUser("http://www.digitalistmag.com/files/2016/01/1926935_55L0dcb.jpg"),
                                               "John",
                                               "Mirror"), page)
            }

    override fun changeUserLikeForPost(feedItem: NewsFeedItem, like: Boolean): Completable {
        return postsRepository.changeUserLikeForPost(feedItem, like)
    }

    override fun answerForPoll(feedItemPoll: NewsFeedItemPoll, pollChoice: PollChoice): Single<NewsFeedItem> {
        return postsRepository.answerForPoll(feedItemPoll, pollChoice)
    }

    override fun sendUserCommentToPost(newsFeedItem: NewsFeedItem, userCommentText: String): Single<Comment> {
        return postsRepository.sendUserCommentToPost(newsFeedItem, userCommentText)
    }
}

private fun generateTestData(currentUser: User, page: Int): List<NewsFeedItem> {
    return listOf(
        NewsFeedItemPoll("${page * 20 + 0}",
                         true,
                         true,
                         currentUser,
                         "Is it true?",
                         listOf(PollChoice("1", "Yes", emptyList()), PollChoice("2", "No", listOf())),
                         DateTime.now(),
                         null,
                         listOf(Audience.FACULTY),
                         emptyList(),
                         emptyList()
        ),
        NewsFeedItemPoll("${page * 20 + 1}",
                         false,
                         true,
                         currentUser,
                         "Is it true?",
                         listOf(PollChoice("1", "Yes", emptyList()),
                                PollChoice("2", "No", listOf(currentUser))),
                         DateTime.now(),
                         DateTime.now().plusWeeks(1),
                         listOf(Audience.STAFF),
                         emptyList(),
                         emptyList()
        ),
        NewsFeedItemPoll("${page * 20 + 2}",
                         false,
                         false,
                         currentUser,
                         "Is it true?",
                         listOf(PollChoice("1", "Yes", listOf(currentUser)),
                                PollChoice("2", "No", listOf(currentUser, currentUser))),
                         DateTime.now(),
                         DateTime.now().plusDays(16),
                         listOf(Audience.NEWS),
                         emptyList(),
                         emptyList()
        ),
        NewsFeedItemAnnouncement("${page * 20 + 15}",
                                 false,
                                 true,
                                 currentUser,
                                 "test message $page",
                                 DateTime.now(),
                                 listOf(Audience.STAFF),
                                 AnnouncementType.EVENT,
                                 emptyList(),
                                 emptyList()
        ),
        NewsFeedItemAnnouncement("${page * 20 + 16}",
                                 true,
                                 true,
                                 currentUser,
                                 "just an other message $page",
                                 DateTime(2017, 12, 7, 15, 20),
                                 listOf(Audience.STAFF),
                                 AnnouncementType.SAFETY,
                                 emptyList(),
                                 listOf(currentUser)
        ),
        NewsFeedItemAnnouncement("${page * 20 + 17}",
                                 true,
                                 false,
                                 currentUser,
                                 "big text message. big text message. big text message. big text message. big text message. big text message. big text message. big text message. big text message. big text message. big text message. big text message. ",
                                 DateTime(2017, 12, 25, 15, 0),
                                 listOf(Audience.STAFF),
                                 AnnouncementType.GENERAL,
                                 emptyList(),
                                 listOf(currentUser, currentUser, currentUser, currentUser, currentUser, currentUser)
        ),
        NewsFeedItemAnnouncement("${page * 20 + 18}",
                                 false,
                                 false,
                                 currentUser,
                                 "",
                                 DateTime(2017, 10, 7, 15, 0),
                                 listOf(Audience.STAFF),
                                 AnnouncementType.GOOD_NEWS,
                                 listOf(Comment("$page ste 1", currentUser,
                                                "just comment!!!",
                                                DateTime.now())),
                                 emptyList()
        ),
        NewsFeedItemAnnouncement("${page * 20 + 19}",
                                 false,
                                 false,
                                 currentUser,
                                 "test message",
                                 DateTime(2017, 10, 1, 15, 0),
                                 listOf(Audience.STAFF),
                                 AnnouncementType.GOOD_NEWS,
            /*"https://www.nytimes.com/2017/12/05/opinion/does-president-trump-want-to-negotiate-middle-east-peace.html?action=click&pgtype=Homepage&clickSource=story-heading&module=opinion-c-col-left-region&region=opinion-c-col-left-region&WT.nav=opinion-c-col-left-region"
           , IconImage("https://static01.nyt.com/images/2017/12/06/opinion/06wed1/06wed1-superJumbo.jpg"),
           "Does President Trump Want to Negotiate Middle East Peace?",
           */
                                 listOf(
                                     Comment("$page ste 2", currentUser,
                                             "comment 1!!!",
                                             DateTime.now()),
                                     Comment("$page ste 3", currentUser,
                                             "comment 2!!!",
                                             DateTime(2017,
                                                      12,
                                                      7,
                                                      20,
                                                      40)),
                                     Comment("$page ste 4", currentUser,
                                             "comment 3!!!",
                                             DateTime(2017,
                                                      12,
                                                      7,
                                                      19,
                                                      40)),
                                     Comment("$page ste 5", currentUser,
                                             "comment 4!!!",
                                             DateTime(2017,
                                                      12,
                                                      6,
                                                      23,
                                                      40)),
                                     Comment("$page ste 6", currentUser,
                                             "comment 5!!!",
                                             DateTime(2017,
                                                      12,
                                                      3,
                                                      20,
                                                      40))),
                                 emptyList()
        )
    )
}