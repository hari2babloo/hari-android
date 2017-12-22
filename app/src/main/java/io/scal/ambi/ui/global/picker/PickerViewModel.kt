package io.scal.ambi.ui.global.picker

import android.app.Activity
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.content.FileProvider
import io.scal.ambi.extensions.FilePathHelper
import io.scal.ambi.extensions.ImageUtils
import javax.inject.Inject

class PickerViewModel @Inject constructor(private val filePathHelper: FilePathHelper,
                                          private val imageUtils: ImageUtils) : ViewModel() {

    private var customPhotoResource: PhotoResource? = null
    private var intentData: Intent? = null

    fun pickAnImage(viewController: PickerViewController, context: Context) {
        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            if (null == customPhotoResource) {
                customPhotoResource = PhotoResource.createInnerMemoryResource(imageUtils.createImageFileWithProvider())
            }

            val imageUri = FileProvider
                .getUriForFile(context, String.format(CAPTURE_IMAGE_FILE_PROVIDER, context.packageName), customPhotoResource!!.file)
            val dialogFragment = PhotoPickerDialogFragment.Builder(imageUri)
                .requestCodes(REQUEST_CODE_OPEN_GALLERY, REQUEST_CODE_OPEN_CAMERA)
                .build()
            viewController.showPickerDialogFragment(dialogFragment)
        } else {
            viewController.startPickerActivity(PhotoPickerDialogFragment.galleryIntent, REQUEST_CODE_OPEN_GALLERY)
        }
    }

    fun onActivityResult(viewController: PickerViewController, requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_OPEN_GALLERY -> if (Activity.RESULT_OK == resultCode) {
                intentData = data
                viewController.askForReadExternalStoragePermission()
            }
            REQUEST_CODE_OPEN_CAMERA  -> if (Activity.RESULT_OK == resultCode) {
                viewController.setPickedImage(customPhotoResource)
            }
        }
    }

    fun onReadExternalStoragePermissionGranted(viewController: PickerViewController) {
        if (null != intentData) {
            val photoResource = filePathHelper.getResultPath(intentData)
            viewController.setPickedImage(photoResource)
        }
    }

    companion object {

        private val CAPTURE_IMAGE_FILE_PROVIDER = "%s.fileprovider"

        private val REQUEST_CODE_OPEN_GALLERY = 1141
        private val REQUEST_CODE_OPEN_CAMERA = 1142
    }
}