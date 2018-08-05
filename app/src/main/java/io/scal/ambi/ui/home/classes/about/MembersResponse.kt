package io.scal.ambi.ui.home.classes.about

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.extensions.notNullOrThrow
import io.scal.ambi.model.data.server.responses.BaseResponse

/**
 * Created by chandra on 04-08-2018.
 */
class MembersResponse : BaseResponse<List<MembersData>>() {

    @SerializedName("users")
    @Expose
    internal var posts1: List<ItemMembers>? = null

    private val posts: List<ItemMembers>?
        get() = posts1

    override fun parse(): List<MembersData> {
        return posts.notNullOrThrow("users").mapNotNull { it.parse() }
    }
}