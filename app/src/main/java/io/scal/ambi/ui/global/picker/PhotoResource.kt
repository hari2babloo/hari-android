package io.scal.ambi.ui.global.picker

import java.io.File
import java.io.Serializable

class PhotoResource private constructor(private val innerMemory: Boolean, private var mFile: File?) : Serializable {

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

        fun createInnerMemoryResource(file: File): PhotoResource {
            return PhotoResource(true, file)
        }

        fun createOutMemoryResource(file: File): PhotoResource {
            return PhotoResource(false, file)
        }

        fun createOutMemoryResource(path: String): PhotoResource {
            return createOutMemoryResource(File(path))
        }
    }
}