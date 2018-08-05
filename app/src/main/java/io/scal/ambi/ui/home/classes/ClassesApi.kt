package io.scal.ambi.ui.home.classes

import io.reactivex.Single
import io.scal.ambi.ui.home.classes.about.MembersResponse
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface ClassesApi {

    @GET("v1/classes")
    fun getClasses(): Single<ClassesResponse>

    @GET("v1/users?")
    fun getUserDetailsById(@QueryMap params: Map<String,String>): Single<MembersResponse>

}

