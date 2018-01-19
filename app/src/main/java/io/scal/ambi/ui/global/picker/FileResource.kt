package io.scal.ambi.ui.global.picker

import java.io.File
import java.io.Serializable

class FileResource private constructor(private val innerMemory: Boolean, private var mFile: File?) : Serializable {

    val file: File
        get() {
            if (null == mFile) {
                throw IllegalStateException("resource was clean up. check all cleanUp() method calls.")
            }
            return mFile!!
        }

    fun cleanUp() {
        if (innerMemory) {
            mFile?.delete()
        }
        mFile = null
    }

    companion object {

        fun createInnerMemoryResource(file: File): FileResource {
            return FileResource(true, file)
        }

        fun createOutMemoryResource(file: File): FileResource {
            return FileResource(false, file)
        }

        fun createOutMemoryResource(path: String): FileResource {
            return createOutMemoryResource(File(path))
        }
    }
}