package io.scal.ambi.model.data.server

import io.reactivex.Single
import io.scal.ambi.model.data.server.responses.newsfeed.PostPollCreationResponse
import io.scal.ambi.model.data.server.responses.newsfeed.PostsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PostsApi {

    @GET("v1/posts/general?populate=profilePicture")
    fun getPostsGeneral(@Query("timeCutoff") lastPostTime: Long?): Single<PostsResponse>

    @POST("v1/update-posts")
    fun postNewStatus(@Body body: StatusCreationRequest): Single<PostPollCreationResponse>

    @POST("v1/announcement-posts")
    fun postNewAnnouncement(@Body body: AnnouncementCreationRequest): Single<PostPollCreationResponse>

    @POST("v1/poll-posts")
    fun postNewPoll(@Body body: PollCreationRequest): Single<PostPollCreationResponse>
}

class StatusCreationRequest(val poster: String,
                            val isPinned: Boolean,
                            val isLocked: Boolean,
                            val textContent: String,
                            val hosts: List<Host>,
                            val audience: List<String>)

class AnnouncementCreationRequest(val poster: String,
                                  val isPinned: Boolean,
                                  val isLocked: Boolean,
                                  val textContent: String,
                                  val hosts: List<Host>,
                                  val announcementType: String,
                                  val audience: List<String>)

class PollCreationRequest(val poster: String,
                          val isPinned: Boolean,
                          val isLocked: Boolean,
                          val textContent: String,
                          val answerChoices: List<ChoiceRequest>,
                          val pollEnds: Long?,
                          val hosts: List<Host>)

class Host(val kind: String, val host: String)

class ChoiceRequest(val text: String)