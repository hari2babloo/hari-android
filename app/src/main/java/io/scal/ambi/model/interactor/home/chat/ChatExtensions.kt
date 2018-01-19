package io.scal.ambi.model.interactor.home.chat

import io.reactivex.Maybe
import io.scal.ambi.entity.chat.ChatAttachment
import io.scal.ambi.entity.chat.ChatMessage
import io.scal.ambi.entity.user.User
import io.scal.ambi.model.data.server.ServerResponseException
import io.scal.ambi.model.repository.data.chat.data.ChatChannelMessage
import io.scal.ambi.model.repository.data.user.IUserRepository
import java.io.File

internal fun ChatChannelMessage.Media.toAttachment(): ChatAttachment =
    if (image) {
        ChatAttachment.Image(url)
    } else {
        ChatAttachment.File(url, fileName.typeName(), size)
    }

private fun String.typeName(): String =
    File(this).extension.toLowerCase()

internal fun getUserProfile(uid: String, userRepository: IUserRepository): Maybe<User> =
    userRepository.getProfileCached(uid).toMaybe().onErrorComplete { it is ServerResponseException && it.notFound && it.serverError }
        .onErrorComplete()// todo remove

internal fun generateChatMessage(message: ChatChannelMessage?, userRepository: IUserRepository): Maybe<ChatMessage> {
    if (null == message) {
        return Maybe.empty()
    }
    return getUserProfile(message.sender, userRepository)
        .map { sender ->
            if (null == message.media) {
                ChatMessage.TextMessage(message.uid, message.index, sender, message.sendDate, message.message)
            } else {
                ChatMessage.AttachmentMessage(message.uid,
                                              message.index,
                                              sender,
                                              message.sendDate,
                                              message.message,
                                              listOf(message.media.toAttachment()))
            }
        }
}