package io.scal.ambi.model.interactor.home.chat

import android.os.SystemClock
import io.reactivex.Maybe
import io.reactivex.Single
import io.scal.ambi.entity.chat.ChatAttachment
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.IconImageUser
import io.scal.ambi.model.repository.data.chat.data.ChatChannelInfo
import io.scal.ambi.model.repository.data.chat.data.ChatChannelMessage
import io.scal.ambi.ui.global.picker.FileResource
import java.io.File
import java.util.*

internal fun ChatChannelMessage.Media.toAttachment(): ChatAttachment =
    if (image) {
        ChatAttachment.Image(url)
    } else {
        ChatAttachment.File(url, fileName.typeName(), size)
    }

private fun String.typeName(): String =
    File(this).extension.toLowerCase()

internal fun FileResource.typeName(): String =
    file.extension.toLowerCase()

internal fun generateChatName(chatInfo: ChatChannelInfo, users: List<User>, currentUser: User?): Single<String> {
    return Single.just(users.filter { it != currentUser }.map { it.name }.fold("", { acc, name -> if (acc.isEmpty()) name else "$acc, $name" }))
}

internal fun generateChatIcon(chatInfo: ChatChannelInfo, members: List<User>, currentUser: User?): Single<IconImage> =
    if (members.isEmpty()) {
        Maybe.empty()
    } else {
        val membersFiltered = members.filter { it != currentUser }
        val member = membersFiltered[Random(SystemClock.currentThreadTimeMillis()).nextInt(membersFiltered.size)]
        Maybe.just(member.avatar as IconImage)
    }
        .toSingle(IconImageUser())
