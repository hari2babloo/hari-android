package io.scal.ambi.ui.home.chat.newmessage

import android.graphics.drawable.Drawable
import android.net.Uri
import com.pchmn.materialchips.model.ChipInterface
import io.scal.ambi.entity.user.User

internal class UIUserChip(val user: User) : ChipInterface {

    override fun getInfo(): String = ""

    override fun getAvatarDrawable(): Drawable? = null

    override fun getLabel(): String = user.name

    override fun getId(): Any = user.uid

    override fun getAvatarUri(): Uri? = Uri.parse(user.avatar.iconPath)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UIUserChip) return false

        return user == other.user
    }

    override fun hashCode(): Int {
        return user.hashCode()
    }
}