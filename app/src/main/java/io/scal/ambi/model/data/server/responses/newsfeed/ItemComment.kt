package io.scal.ambi.model.data.server.responses.newsfeed

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.entity.actions.Comment
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.notNullOrThrow
import io.scal.ambi.model.data.server.responses.user.ItemUser
import io.scal.ambi.model.data.server.responses.Parceble
import org.joda.time.DateTime

internal class ItemComment : Parceble<Comment> {

    @SerializedName("_id")
    @Expose
    internal var id: String? = null

    @SerializedName("text")
    @Expose
    internal var text: String? = null

    @SerializedName("commenter")
    @Expose
    internal var poster: ItemUser? = null

    @SerializedName("dateCreated")
    @Expose
    internal var createdAt: Long? = null

    override fun parse(): Comment {
        return Comment(id.notNullOrThrow("id"),
                       parsePoster(),
                       text.orEmpty(),
                       DateTime(createdAt!!)
        )
    }

    private fun parsePoster(): User {
        return poster.notNullOrThrow("commenter").parse()
    }
}