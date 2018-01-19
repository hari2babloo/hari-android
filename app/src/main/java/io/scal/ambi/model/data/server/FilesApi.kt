package io.scal.ambi.model.data.server

import io.reactivex.Single
import io.scal.ambi.model.data.server.responses.FileResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface FilesApi {

    @POST("v1/files")
    fun createFile(@Body request: FileCreationRequest): Single<FileResponse>

    class FileCreationRequest(val name: String, val url: String, val fileType: String)
}