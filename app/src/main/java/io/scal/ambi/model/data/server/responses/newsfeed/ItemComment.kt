package io.scal.ambi.model.data.server.responses.newsfeed

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.entity.actions.Comment
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.notNullOrThrow
import io.scal.ambi.extensions.view.IconImageUser
import io.scal.ambi.model.data.server.responses.ItemUser
import io.scal.ambi.model.data.server.responses.Parceble
import org.joda.time.DateTime

internal class ItemComment : Parceble<Comment> {

    @SerializedName("_id")
    @Expose
    internal var id: String? = null

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
        return Comment(id.notNullOrThrow("id"), parsePoster(), text.orEmpty(), DateTime(createdAt!!))
    }

    // todo change to actual user
    private fun parsePoster(): User {
//        poster.notNullOrThrow("poster").parse()
        return User.asStudent("sdfsfd",
                              IconImageUser("http://www.avatardiscoverpandora.com/wp-content/uploads/2017/05/GES_AVATAR_SLIDER_Image_2560x1080px_NEW_03.jpg"),
                              "MIG35",
                              "TEST")
    }
}