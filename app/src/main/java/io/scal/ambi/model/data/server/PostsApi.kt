package io.scal.ambi.model.data.server

import io.reactivex.Completable
import io.reactivex.Single
import io.scal.ambi.model.data.server.responses.newsfeed.PostsResponse
import retrofit2.http.*

interface PostsApi {

    @GET("v1/posts/general/populate?timeCutoff=&getPicture=true&populate[]=poster&populate[]=fileContent&populate[]=likes&populate[]=hosts.host&populate[]=comments.commenter")
    fun getPostsGeneral(@Query("page") page: Long, @Query("filter") filter: String): Single<PostsResponse>


    @POST("v1/update-posts")
    fun postNewStatus(@Body body: StatusCreationRequest): Completable

    @GET("v1/update-posts/{postId}")
    fun getStatusPostAsString(@Path("postId") postId: String): Single<String>

    @PUT("v1/update-posts/{postId}")
    fun updateStatusPost(@Path("postId") postId: String, @Header("Content-Type") contentType: String, @Body body: String): Completable


    @POST("v1/announcement-posts")
    fun postNewAnnouncement(@Body body: AnnouncementCreationRequest): Completable

    @GET("v1/announcement-posts/{postId}")
    fun getAnnouncementPostAsString(@Path("postId") postId: String): Single<String>

    @PUT("v1/announcement-posts/{postId}")
    fun updateAnnouncementPost(@Path("postId") postId: String, @Header("Content-Type") contentType: String, @Body body: String): Completable


    @POST("v1/poll-posts")
    fun postNewPoll(@Body body: PollCreationRequest): Completable

    @GET("v1/poll-posts/{postId}")
    fun getPollPostAsString(@Path("postId") postId: String): Single<String>

    @PUT("v1/poll-posts/{postId}")
    fun updatePollPost(@Path("postId") postId: String, @Header("Content-Type") contentType: String, @Body body: String): Completable
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