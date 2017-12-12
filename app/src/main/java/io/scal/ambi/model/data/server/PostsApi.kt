package io.scal.ambi.model.data.server

import io.reactivex.Single
import io.scal.ambi.model.data.server.responses.EmptyResponse
import io.scal.ambi.model.data.server.responses.PostsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PostsApi {

    @GET("posts/general")
    fun getPostsGeneral(@Query("timeCutoff") lastPostTime: Long?): Single<PostsResponse>

    @POST("poll-posts")
    fun postNewPoll(@Body body: PollCreationRequest): Single<EmptyResponse>
}

class PollCreationRequest(val poster: String,
                          val isPinned: Boolean,
                          val isLocked: Boolean,
                          val textContent: String,
                          val audience: List<String>,
                          val answerChoices: List<ChoiceRequest>,
                          val pollEnds: Long?)

class ChoiceRequest(val text: String)