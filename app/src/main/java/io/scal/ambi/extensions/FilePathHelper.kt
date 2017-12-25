package io.scal.ambi.extensions

import android.annotation.TargetApi
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.os.Parcelable
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import io.scal.ambi.ui.global.picker.FileResource
import java.io.IOException
import javax.inject.Inject


class FilePathHelper @Inject constructor(private val context: Context, private val imageUtils: ImageUtils) {

    fun getResultPath(data: Intent?): FileResource? {
        // for 5.0 versions
        val selectedImageUri = data?.data
        if (null != selectedImageUri) {
            val fileName = getFileNameWithExtension(context, selectedImageUri)

            var parcelFileDescriptor: ParcelFileDescriptor? = null
            try {
                parcelFileDescriptor = context.contentResolver.openFileDescriptor(selectedImageUri, "r")
                if (null != parcelFileDescriptor) {
                    val fileDescriptor = parcelFileDescriptor.fileDescriptor
                    val imageFile = imageUtils.copyFdToFile(fileDescriptor, fileName)
                    if (null != imageFile) {
                        return FileResource.createInnerMemoryResource(imageFile)
                    }
                }
            } catch (ignored: IOException) {
                //pass
            } finally {
                if (null != parcelFileDescriptor) {
                    try {
                        parcelFileDescriptor.close()
                    } catch (ignored: IOException) {
                        // pass
                    }
                }
            }

            val photoResource = getFromCursor(context, selectedImageUri)
            if (null != photoResource) {
                return photoResource
            }
        }

        val bitmapParcelable = data?.getParcelableExtra<Parcelable>("data")
        if (bitmapParcelable is Bitmap) {
            val bitmap = bitmapParcelable as Bitmap?
            val imageFile = imageUtils.saveBitmap(bitmap!!)
            bitmap.recycle()
            if (null != imageFile) {
                return FileResource.createInnerMemoryResource(imageFile)
            }
        }

        return null
    }

    private fun getFileNameWithExtension(context: Context, uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (it != null && it.moveToFirst()) {
                    result = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/') ?: -1
            if (cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        if (null != result) {
            val extension = context.contentResolver.getType(uri)?.let { MimeTypeMap.getSingleton().getExtensionFromMimeType(it) }
            result = extension?.let { result.plus(".").plus(it) } ?: result
        }

        return result
    }

    companion object {

        private fun getFromCursor(context: Context, selectedData: Uri?): FileResource? {
            if (null == selectedData) {
                return null
            }
            try {
                val filePathColumn = arrayOf(MediaStore.MediaColumns.DATA)

                val cursor = context.contentResolver.query(selectedData, filePathColumn, null, null, null)

                cursor?.use {
                    if (it.moveToFirst()) {
                        val imagePathString = it.getString(it.getColumnIndex(filePathColumn[0]))
                        if (null != imagePathString) {
                            return FileResource.createOutMemoryResource(imagePathString)
                        }
                    }
                }
                val pathAsString = getPath(context, selectedData)
                if (null != pathAsString) {
                    return FileResource.createOutMemoryResource(pathAsString)
                }
            } catch (ignored: Exception) {
                // pass
            }

            return null
        }

        /**
         * Get a file path from a Uri. This will get the the path for Storage Access
         * Framework Documents, as well as the _data field for the MediaStore and
         * other file-based ContentProviders.
         *
         * @param context The context.
         * @param uri     The Uri to query.
         */
        @TargetApi(Build.VERSION_CODES.KITKAT)
        private fun getPath(context: Context, uri: Uri): String? {

            val isKitKat = Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT

            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]

                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    }

                    // TODO handle non-primary volumes
                } else if (isDownloadsDocument(uri)) {

                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)!!)

                    return getDataColumn(context, contentUri, null, null)
                } else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]

                    var contentUri: Uri? = null
                    when (type) {
                        "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }

                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])

                    return getDataColumn(context, contentUri, selection, selectionArgs)
                }// MediaProvider
                // DownloadsProvider
            } else if ("content".equals(uri.scheme, ignoreCase = true)) {
                return getDataColumn(context, uri, null, null)
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }// File
            // MediaStore (and general)

            return null
        }

        /**
         * Get the value of the data column for this Uri. This is useful for
         * MediaStore Uris, and other file-based ContentProviders.
         *
         * @param context       The context.
         * @param uri           The Uri to query.
         * @param selection     (Optional) Filter used in the query.
         * @param selectionArgs (Optional) Selection arguments used in the query.
         * @return The value of the _data column, which is typically a file path.
         */
        private fun getDataColumn(context: Context, uri: Uri?, selection: String?,
                                  selectionArgs: Array<String>?): String? {

            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(column)

            try {
                cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
                if (null != cursor && cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(columnIndex)
                }
            } finally {
                if (null != cursor) {
                    cursor.close()
                }
            }
            return null
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        private fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        private fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        private fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }
    }
}