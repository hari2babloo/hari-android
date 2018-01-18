package io.scal.ambi.model.data.server.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.entity.base.ServerFile
import io.scal.ambi.extensions.notNullOrThrow

class FileResponse : BaseResponse<ServerFile>() {

    @SerializedName("file")
    @Expose
    var file: ItemFile? = null

    override fun parse(): ServerFile {
        return file.notNullOrThrow("file").parse()
    }

    class ItemFile : Parceble<ServerFile> {

        @SerializedName("_id")
        @Expose
        var id: String? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("url")
        @Expose
        var url: String? = null

        @SerializedName("fileType")
        @Expose
        var fileType: String? = null

        @SerializedName("createdAt")
        @Expose
        var createdAt: String? = null

        override fun parse(): ServerFile {
            return ServerFile(id.notNullOrThrow("id"),
                              url.notNullOrThrow("url")
            )
        }
    }
}