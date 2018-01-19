package io.scal.ambi.model.data.server.responses

import com.ambi.work.BuildConfig
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.extensions.notNullOrThrow
import io.scal.ambi.extensions.view.IconImage

class ItemPicture {

    @SerializedName("_id")
    @Expose
    var _id: String? = null

    @SerializedName("url")
    @Expose
    var url: String? = null

    fun parse(): IconImage {
        return url.notNullOrThrow("url")
            .let { if (it.startsWith("http")) it else BuildConfig.MAIN_IMAGES_URL + it }
            .let { IconImage(it) }
    }
}