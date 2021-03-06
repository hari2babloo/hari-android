package io.scal.ambi.model.data.server

import io.reactivex.Completable
import io.reactivex.Single
import io.scal.ambi.model.data.server.responses.newsfeed.PostsResponse
import io.scal.ambi.ui.home.notifications.NotificationResponse
import retrofit2.http.*

interface PostsApi {

    @GET("v1/posts?timeCutoff=&getPicture=true&populate[]=poster&populate[]=fileContent&populate[]=likes&populate[]=hosts.host&populate[]=comments.commenter")
    fun getPostsGeneral(@Query("entityType") entityType: String, @Query("pageNum") page: Long, @Query("my_category") filter: String): Single<PostsResponse>

    //@GET("v1/posts/personal?timeCutoff=&getPicture=true&populate[]=poster&populate[]=fileContent&populate[]=likes&populate[]=hosts.host&populate[]=comments.commenter")
    @GET("v1/posts?timeCutoff=&getPicture=true&populate[]=poster&populate[]=fileContent&populate[]=likes&populate[]=hosts.host&populate[]=comments.commenter")
    fun getPostsPersonal(@Query("entityType") entityType: String, @Query("pageNum") page: Long): Single<PostsResponse>

    @GET("v1/posts/entity/{userId}?timeCutoff=&getPicture=true&populate[]=poster&populate[]=fileContent&populate[]=likes&populate[]=hosts.host&populate[]=comments.commenter")
    fun getPostsForUser(@Path("userId") userId: String, @Query("page") page: Long): Single<PostsResponse>

    @GET("v1/rss-feed/0")
    fun getLatestNews(): Single<PostsResponse>


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

    //@GET("v1/notifications")
    //fun loadNotification(@Query("pageNum") page: Int,@Query("category") category: String): Single<NotificationResponse>

    @GET("v1/notifications")
    fun loadNotification(): Single<NotificationResponse>
}

class StatusCreationRequest(val poster: String,
                            val isPinned: Boolean,
                            val isLocked: Boolean,
                            val textContent: String,
                            val hosts: List<Host>,
                            val audience: List<String>,
                            val fileContent: List<String>?)

class AnnouncementCreationRequest(val poster: String,
                                  val isPinned: Boolean,
                                  val isLocked: Boolean,
                                  val textContent: String,
                                  val hosts: List<Host>,
                                  val announcementType: String,
                                  val audience: List<String>,
                                  val fileContent: List<String>?)

class PollCreationRequest(val poster: String,
                          val isPinned: Boolean,
                          val isLocked: Boolean,
                          val textContent: String,
                          val answerChoices: List<ChoiceRequest>,
                          val pollEnds: Long?,
                          val hosts: List<Host>)

class Host(val kind: String, val host: String)

class ChoiceRequest(val text: String)