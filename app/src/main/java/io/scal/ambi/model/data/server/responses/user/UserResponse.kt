package io.scal.ambi.model.data.server.responses.user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.entity.user.User
import io.scal.ambi.entity.user.UserResume
import io.scal.ambi.entity.user.WorkExperience
import io.scal.ambi.extensions.notNullOrThrow
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.model.data.server.responses.BaseResponse
import io.scal.ambi.model.data.server.responses.ItemPicture
import io.scal.ambi.model.data.server.responses.Parceble

class UserResponse : BaseResponse<User>() {

    @SerializedName("user")
    @Expose
    internal var user: BigUser? = null

    override fun parse(): User {
        return user.notNullOrThrow("user").parse()
    }

    fun parseAsResume(): UserResume {
        val goodUser = user.notNullOrThrow("user")
        return UserResume(goodUser.pitch.orEmpty())
    }

    class BigUser : ItemUser() {

        @SerializedName("school")
        @Expose
        var schoolId: String? = null

        @SerializedName("gender")
        @Expose
        var gender: String? = null

        @SerializedName("bannerPicture")
        @Expose
        var bannerPicture: ItemPicture? = null

        @SerializedName("workExperience")
        @Expose
        var workExperience: List<ItemWorkExperience>? = null

        @SerializedName("pitch")
        @Expose
        var pitch: String? = null

        override fun parse(): User {
            val user = super.parse()
            return user.copy(banner = parseBanner(), workExperience = parseWorkExperience(), liveAt = "Wellesley, MA")
        }

        private fun parseBanner(): IconImage? {
            return bannerPicture?.parse()
        }

        private fun parseWorkExperience(): List<WorkExperience>? {
            val convertedExp = workExperience?.map { it.parse() }
            return if (null == convertedExp || convertedExp.isEmpty()) null else convertedExp
        }
    }

    class ItemWorkExperience : Parceble<WorkExperience> {

        @SerializedName("title")
        @Expose
        var title: String? = null

        @SerializedName("description")
        @Expose
        var description: String? = null

        override fun parse(): WorkExperience {
            return WorkExperience(title.orEmpty(), description.orEmpty())
        }
    }
}