package io.scal.ambi.ui.home.newsfeed

import io.scal.ambi.extensions.view.IconImage

internal sealed class ModelFeedElement(val uid: String,
                                       val actor: String,
                                       val icon: IconImage) {

    class Message(uid: String,
                  actor: String,
                  icon: IconImage,
                  val message: String) : ModelFeedElement(uid, actor, icon)

    class Link(uid: String,
               actor: String,
               icon: IconImage,
               val message: String,
               val linkUri: String,
               val linkPreviewImage: IconImage?,
               val linkTitle: String) : ModelFeedElement(uid, actor, icon)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ModelFeedElement) return false

        return uid == other.uid
    }

    override fun hashCode(): Int =
        uid.hashCode()
}