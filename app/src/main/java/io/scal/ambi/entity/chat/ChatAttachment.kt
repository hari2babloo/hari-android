package io.scal.ambi.entity.chat

import java.io.Serializable
import java.net.URI

sealed class ChatAttachment(open val path: URI) : Serializable {

    data class Image(override val path: URI) : ChatAttachment(path) {

        constructor(path: String) : this(URI(path))
    }

    data class File(override val path: URI,
                    val typeName: String,
                    val typeIcon: String,
                    val size: Long) : ChatAttachment(path) {

        constructor(path: String, typeName: String, typeIcon: String, size: Long) : this(URI(path), typeName, typeIcon, size)
    }
}