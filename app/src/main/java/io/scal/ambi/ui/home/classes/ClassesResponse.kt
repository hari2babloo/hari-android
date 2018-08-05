package io.scal.ambi.ui.home.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.extensions.notNullOrThrow
import io.scal.ambi.model.data.server.responses.BaseResponse

class ClassesResponse : BaseResponse<List<ClassesData>>() {

    @SerializedName("classes")
    @Expose
    internal var posts1: List<ItemClasses>? = null

    private val posts: List<ItemClasses>?
        get() = posts1

    override fun parse(): List<ClassesData> {
        return posts.notNullOrThrow("classes").mapNotNull { it.parse() }
    }
}