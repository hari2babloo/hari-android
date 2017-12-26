package io.scal.ambi.ui.home.chat.newmessage

import android.graphics.drawable.Drawable
import android.net.Uri
import com.pchmn.materialchips.model.ChipInterface
import io.scal.ambi.entity.user.User

internal class UIUserChip(val user: User) : ChipInterface {

    override fun getInfo(): String = "unknown TODO!!!"

    override fun getAvatarDrawable(): Drawable? = null

    override fun getLabel(): String = user.name

    override fun getId(): Any = user.uid

    override fun getAvatarUri(): Uri? = Uri.parse(user.avatar.iconPath)
}