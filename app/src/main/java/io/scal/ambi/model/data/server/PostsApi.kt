package io.scal.ambi.model.data.server

import io.reactivex.Single
import io.scal.ambi.model.data.server.responses.PostsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PostsApi {

    @GET("posts/general")
    fun getPostsGeneral(@Query("timeCutoff") lastPostTime: Long): Single<PostsResponse>
}