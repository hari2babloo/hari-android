package io.scal.ambi.entity.chat

import java.io.Serializable
import java.net.URI

data class ChatAttachment(val path: URI,
                          val type: ChatAttachmentType) : Serializable {

    constructor(path: String, type: ChatAttachmentType) : this(URI(path), type)
}