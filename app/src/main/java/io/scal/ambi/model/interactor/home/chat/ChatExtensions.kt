package io.scal.ambi.model.interactor.home.chat

import io.reactivex.Maybe
import io.scal.ambi.entity.chat.ChatAttachment
import io.scal.ambi.entity.chat.ChatMessage
import io.scal.ambi.entity.user.User
import io.scal.ambi.model.data.server.ServerResponseException
import io.scal.ambi.model.repository.data.chat.data.ChatChannelMessage
import io.scal.ambi.model.repository.data.user.IUserRepository
import io.scal.ambi.ui.global.picker.FileResource
import java.io.File

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

internal fun getUserProfile(uid: String, userRepository: IUserRepository): Maybe<User> =
    userRepository.getProfileCached(uid).toMaybe().onErrorComplete { it is ServerResponseException && it.notFound && it.serverError }

internal fun generateChatMessage(message: ChatChannelMessage?, userRepository: IUserRepository): Maybe<ChatMessage> {
    if (null == message) {
        return Maybe.empty()
    }
    return getUserProfile(message.sender, userRepository)
        .map { sender ->
            if (null == message.media) {
                ChatMessage.TextMessage(message.uid, sender, message.sendDate, message.message)
            } else {
                ChatMessage.AttachmentMessage(message.uid, sender, message.sendDate, message.message, listOf(message.media.toAttachment()))
            }
        }
}