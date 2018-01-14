package io.scal.ambi.entity.chat

import android.net.Uri
import io.scal.ambi.ui.global.picker.FileResource
import java.io.Serializable

sealed class ChatAttachment(open val pathAsString: String) : Serializable {

    val path: Uri
        get() = Uri.parse(pathAsString)

    open class Image(path: String) : ChatAttachment(path) {

        constructor(path: Uri) : this(path.toString())
    }

    class LocalImage(val imageFile: FileResource) : Image(Uri.fromFile(imageFile.file))

    open class File(path: String,
                    val typeName: String,
                    val size: Long) : ChatAttachment(path) {

        constructor(path: Uri, typeName: String, size: Long) : this(path.toString(), typeName, size)
    }

    class LocalFile(val fileFile: FileResource,
                    typeName: String,
                    size: Long) : File(Uri.fromFile(fileFile.file), typeName, size)
}