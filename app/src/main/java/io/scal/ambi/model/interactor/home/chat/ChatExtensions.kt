package io.scal.ambi.model.interactor.home.chat

import io.scal.ambi.entity.chat.ChatAttachment
import io.scal.ambi.model.repository.data.chat.ChatChannelMessage
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