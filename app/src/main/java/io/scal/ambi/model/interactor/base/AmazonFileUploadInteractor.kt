package io.scal.ambi.model.interactor.base

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.internal.StaticCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.ObjectMetadata
import io.reactivex.Single
import io.scal.ambi.entity.base.ServerFile
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.ImageUtils
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.data.server.FilesApi
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import io.scal.ambi.ui.global.picker.FileResource
import java.io.*
import java.util.*
import javax.inject.Inject


private const val s3AccessKey = "AKIAJKNZZ63HCXAU5NFA"
private const val s3SecretKey = "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"
private const val s3Bucket = "ambi-dev"
private val s3Region = Regions.US_EAST_1

class AmazonFileUploadInteractor @Inject constructor(private val context: Context,
                                                     private val localUserDataRepository: ILocalUserDataRepository,
                                                     private val imageUtils: ImageUtils,
                                                     private val filesApi: FilesApi,
                                                     private val rxSchedulersAbs: RxSchedulersAbs) : IFileUploadInteractor {

    private val syncObject = Object()

    private val transferUtility: TransferUtility
        by lazy {
            val credProvider = StaticCredentialsProvider(BasicAWSCredentials(s3AccessKey, s3SecretKey))
            val client = AmazonS3Client(credProvider)
            client.setRegion(Region.getRegion(s3Region))

            TransferUtility.builder()
                .defaultBucket(s3Bucket)
                .s3Client(client)
                .context(context)
                .build()
        }

    override fun uploadFile(fileResource: FileResource): Single<ServerFile> {
        return localUserDataRepository
            .observeCurrentUser()
            .firstOrError()
            .flatMap { uploadFile(fileResource, it) }
    }

    override fun uploadFile(fileResource: FileResource, creatorUser: User): Single<ServerFile> {
        return uploadFileInner(fileResource, false, null)
    }

    override fun uploadImage(fileResource: FileResource, maxFileSizeInPx: Int?): Single<ServerFile> {
        return localUserDataRepository
            .observeCurrentUser()
            .firstOrError()
            .flatMap { uploadImage(fileResource, it, maxFileSizeInPx) }
    }

    override fun uploadImage(fileResource: FileResource, creatorUser: User, maxFileSizeInPx: Int?): Single<ServerFile> {
        return uploadFileInner(fileResource, true, maxFileSizeInPx)
    }

    private fun uploadFileInner(fileResource: FileResource, image: Boolean, maxFileSizeInPx: Int?): Single<ServerFile> {
        val fileKey = UUID.randomUUID().toString() + "." + fileResource.file.extension
        val fileType = if (image) "jpg" else fileResource.file.extension
        return Single.create<Pair<Int, Long>> { e ->
            val objectMetaData = ObjectMetadata()
            objectMetaData.contentType = if (image) "image/$fileType" else "application/$fileType"
            val compressedFile = if (image && null != maxFileSizeInPx) createCompressedFile(fileResource, maxFileSizeInPx) else fileResource.file

            transferUtility.upload(fileKey, compressedFile, objectMetaData, CannedAccessControlList.PublicRead, object : TransferListener {

                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {}

                override fun onStateChanged(id: Int, state: TransferState) {
                    when (state) {
                        TransferState.COMPLETED           -> {
                            if (!e.isDisposed) {
                                if (null != maxFileSizeInPx) compressedFile.delete()
                                e.onSuccess(Pair(id, compressedFile.length()))
                            }
                        }
                        TransferState.CANCELED            -> {
                            if (!e.isDisposed) {
                                if (null != maxFileSizeInPx) compressedFile.delete()
                                e.onError(IOException("cancelled!"))
                            }
                        }
                        TransferState.WAITING_FOR_NETWORK -> transferUtility.cancel(id)
                        TransferState.FAILED              -> {
                            transferUtility.deleteTransferRecord(id)
                        }
                    }
                }

                override fun onError(id: Int, ex: Exception) {
                    if (!e.isDisposed) {
                        if (null != maxFileSizeInPx) compressedFile.delete()
                        e.onError(ex)
                    }
                }
            })
        }
            .subscribeOn(rxSchedulersAbs.ioScheduler)
            .observeOn(rxSchedulersAbs.ioScheduler)
            .map { S3ServerFile(fileResource.file.nameWithoutExtension, "https://s3.amazonaws.com/$s3Bucket/$fileKey", it.second) }
            .flatMap { filesApi.createFile(FilesApi.FileCreationRequest(it.name, it.url, fileType)) }
            .map { it.parse() }
    }

    private fun createCompressedFile(fileResource: FileResource, imageMaxSize: Int): File {
        val finalResultFile = imageUtils.createImageFileWithProvider()
        try {
            BufferedOutputStream(FileOutputStream(finalResultFile, false))
                .use {
                    writeCompressedFileToStream(fileResource.file, imageMaxSize, Bitmap.CompressFormat.JPEG, 80, it)

                    it.flush()
                }
            return finalResultFile
        } catch (e: Exception) {
            finalResultFile.delete()
            throw e
        }
    }

    @Throws(IOException::class)
    private fun writeCompressedFileToStream(file: File,
                                            maxSize: Int,
                                            compressFormat: Bitmap.CompressFormat,
                                            quality: Int,
                                            outputStream: OutputStream) {
        synchronized(syncObject) {
            val options = BitmapFactory.Options()
            options.inSampleSize = createSampleSizeForImageSizes(file.absolutePath, maxSize, options)

            val bitmap = createScaledBitmap(file, maxSize, options)

            val bitmapToUpload = createRotatedBitmap(file, bitmap)

            bitmapToUpload.compress(compressFormat, quality, outputStream)
            bitmapToUpload.recycle()

            System.gc()
        }
    }

    @Throws(IOException::class)
    private fun createScaledBitmap(file: File, maxSize: Int, options: BitmapFactory.Options): Bitmap {
        var doCompressWork: Boolean
        var bitmap: Bitmap? = null
        do {

            try {
                bitmap = BitmapFactory.decodeFile(file.absolutePath, options)

                if (bitmap!!.width > maxSize || bitmap.height > maxSize) {
                    bitmap = generateScaledBitmap(bitmap, maxSize)
                }

                doCompressWork = false
            } catch (ignored: OutOfMemoryError) {
                options.inSampleSize *= 2
                doCompressWork = true


                System.gc()
            }

        } while (doCompressWork)

        System.gc()

        if (null == bitmap) {
            throw IOException("can't create image from file! ignore!")
        }

        return bitmap
    }

    private fun createRotatedBitmap(file: File, bitmap: Bitmap): Bitmap {
        val imageRotation = getFileRotation(file)
        val bitmapToUpload: Bitmap
        if (0 == imageRotation) {
            bitmapToUpload = bitmap
        } else {
            val matrix = Matrix()
            matrix.postRotate(imageRotation.toFloat())
            try {
                bitmapToUpload = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            } finally {
                bitmap.recycle()
            }
        }

        System.gc()

        return bitmapToUpload
    }

    private fun getFileRotation(file: File): Int {
        try {
            val exif = ExifInterface(file.absolutePath)

            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)

            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90  -> return 90
                ExifInterface.ORIENTATION_ROTATE_180 -> return 180
                ExifInterface.ORIENTATION_ROTATE_270 -> return 270
            }
        } catch (ignored: IOException) {
            // pass
        }

        return 0
    }

    private fun generateScaledBitmap(bitmap: Bitmap, maxSize: Int): Bitmap? {
        val scale = Math.min(1f * maxSize / bitmap.width, 1f * maxSize / bitmap.height)
        var returnBitmap: Bitmap? = null
        try {
            returnBitmap = Bitmap.createScaledBitmap(bitmap, Math.round(bitmap.width * scale), Math.round(bitmap.height * scale), true)
        } finally {
            if (!Objects.equals(bitmap, returnBitmap)) {
                bitmap.recycle()
            }
        }
        return returnBitmap
    }

    private fun createSampleSizeForImageSizes(filePath: String, maxSize: Int, options: BitmapFactory.Options): Int {
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)

        val sampleSize = Math.min(1f * options.outWidth / maxSize, 1f * options.outHeight / maxSize)
        options.inJustDecodeBounds = false


        return sampleSize.toInt()
    }
}

private data class S3ServerFile(val name: String, val url: String, val size: Long)
