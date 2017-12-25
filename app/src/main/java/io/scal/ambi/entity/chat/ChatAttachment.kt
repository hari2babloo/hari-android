package io.scal.ambi.entity.chat

import android.net.Uri
import io.scal.ambi.ui.global.picker.FileResource
import java.io.Serializable

sealed class ChatAttachment(open val path: Uri) : Serializable {

    open class Image(override val path: Uri) : ChatAttachment(path) {

        constructor(path: String) : this(Uri.parse(path))
    }

    class LocalImage(val imageFile: FileResource) : Image(Uri.fromFile(imageFile.file))

    open class File(override val path: Uri,
                    val typeName: String,
                    val typeIcon: String,
                    val size: Long) : ChatAttachment(path) {

        constructor(path: String, typeName: String, typeIcon: String, size: Long) : this(Uri.parse(path), typeName, typeIcon, size)
    }

    class LocalFile(val fileFile: FileResource,
                    typeName: String,
                    typeIcon: String,
                    size: Long) : File(Uri.fromFile(fileFile.file), typeName, typeIcon, size)
}