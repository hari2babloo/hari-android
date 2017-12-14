package io.scal.ambi.model.data.server.responses.newsfeed

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.entity.actions.Comment
import io.scal.ambi.model.data.server.responses.ItemUser
import io.scal.ambi.model.data.server.responses.Parceble
import org.joda.time.DateTime

internal class ItemComment : Parceble<Comment> {

    @SerializedName("text")
    @Expose
    internal var text: String? = null

    @SerializedName("poster")
    @Expose
    internal var poster: ItemUser? = null

    @SerializedName("dateCreated")
    @Expose
    internal var createdAt: Long? = null

    override fun parse(): Comment {
        return Comment(poster!!.parse(), text.orEmpty(), DateTime(createdAt!!))
    }
}