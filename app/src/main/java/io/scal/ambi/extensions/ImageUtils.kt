package io.scal.ambi.extensions

import android.content.Context
import android.graphics.Bitmap
import timber.log.Timber
import java.io.*
import java.util.*
import javax.inject.Inject

class ImageUtils @Inject constructor(private val context: Context) {

    fun createImageFileWithProvider(fileName: String? = null): File {
        val storageDir = File(context.filesDir, "cache/tmp") // look in image_path.xml
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        return createImageFileFromPath(storageDir, fileName)
    }

    fun copyFdToFile(src: FileDescriptor?, fileName: String? = null): File? {
        if (null == src) {
            return null
        }
        val tmpFile = createImageFileWithProvider(fileName)
        try {
            copyFdToFile(src, tmpFile)
            return tmpFile
        } catch (ignored: Exception) {

            tmpFile.delete()
        }

        return null
    }

    fun saveBitmap(bitmap: Bitmap, fileName: String? = null): File? {
        val tmpFile = createImageFileWithProvider(fileName)
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(tmpFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            return tmpFile
        } catch (ignored: IOException) {
            tmpFile.delete()
        } finally {
            try {
                fos?.close()
            } catch (ignored: IOException) {
                // pass because we don't care
            }
        }
        return null
    }

    @Throws(IOException::class)
    private fun copyFdToFile(src: FileDescriptor, dst: File) =
        FileInputStream(src).copyStreamToFile(dst)

    private fun createImageFileFromPath(storageDir: File, fileName: String? = null): File {
        val tmpFile = File(storageDir, fileName ?: String.format(Locale.US, TEMP_IMAGE_FILE, Date().time))
        if (!tmpFile.exists() || tmpFile.delete()) {
            return tmpFile
        }
        Timber.w("can not create file for image")
        return tmpFile
    }

    companion object {

        private val TEMP_IMAGE_FILE = "RR_TEMP_IMAGE_%d.jpg"
    }
}
