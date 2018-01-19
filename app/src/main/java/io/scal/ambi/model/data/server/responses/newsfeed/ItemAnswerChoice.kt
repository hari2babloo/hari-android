package io.scal.ambi.model.data.server.responses.newsfeed

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.entity.feed.PollChoice
import io.scal.ambi.model.data.server.responses.Parceble
import java.util.*

internal class ItemAnswerChoice : Parceble<PollChoice> {

    @SerializedName("_id")
    @Expose
    internal var id: String? = null

    @SerializedName("text")
    @Expose
    internal var text: String? = null

    @SerializedName("voters")
    @Expose
    internal var voters: List<String>? = null

    override fun parse(): PollChoice {
        return PollChoice(UUID.randomUUID().toString(), /*id.notNullOrThrow("id"), */text.orEmpty(), voters ?: emptyList())
    }
}