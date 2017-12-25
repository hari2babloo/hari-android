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

    private var customFileResource: FileResource? = null

    private var intentData: IntentDataResult? = null

    fun pickFile(viewController: PickerViewController) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        viewController.startActivityForResult(intent, REQUEST_CODE_OPEN_FILE)
    }

    fun pickAnImage(viewController: PickerViewController, context: Context) {
        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            if (null == customFileResource) {
                customFileResource = FileResource.createInnerMemoryResource(imageUtils.createImageFileWithProvider())
            }

            val imageUri = FileProvider
                .getUriForFile(context, String.format(CAPTURE_IMAGE_FILE_PROVIDER, context.packageName), customFileResource!!.file)
            val dialogFragment = PhotoPickerDialogFragment.Builder(imageUri)
                .requestCodes(REQUEST_CODE_OPEN_GALLERY, REQUEST_CODE_OPEN_CAMERA)
                .build()
            viewController.showPickerDialogFragment(dialogFragment)
        } else {
            viewController.startActivityForResult(PhotoPickerDialogFragment.galleryIntent, REQUEST_CODE_OPEN_GALLERY)
        }
    }

    fun onActivityResult(viewController: PickerViewController, requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_OPEN_GALLERY ->
                if (Activity.RESULT_OK == resultCode && null != data) {
                    intentData = IntentDataResult(data, true)
                    viewController.askForReadExternalStoragePermission()
                }
            REQUEST_CODE_OPEN_CAMERA  ->
                if (Activity.RESULT_OK == resultCode) {
                    customFileResource?.run { viewController.setPickedFile(this, true) }
                }
            REQUEST_CODE_OPEN_FILE    ->
                if (Activity.RESULT_OK == resultCode && null != data) {
                    intentData = IntentDataResult(data, false)
                    viewController.askForReadExternalStoragePermission()
                }
        }
    }

    fun onReadExternalStoragePermissionGranted(viewController: PickerViewController) {
        if (null != intentData) {
            filePathHelper.getResultPath(intentData?.intent)?.run { viewController.setPickedFile(this, intentData!!.image) }
            intentData = null
        }
    }

    private class IntentDataResult(val intent: Intent, val image: Boolean)

    companion object {

        private val CAPTURE_IMAGE_FILE_PROVIDER = "%s.fileprovider"

        private val REQUEST_CODE_OPEN_GALLERY = 1141
        private val REQUEST_CODE_OPEN_CAMERA = 1142
        private val REQUEST_CODE_OPEN_FILE = 1143
    }
}