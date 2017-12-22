package io.scal.ambi.ui.global.picker

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import com.azoft.injectorlib.InjectSavedState
import io.scal.ambi.R
import io.scal.ambi.ui.global.base.fragment.BaseDialogFragment

class PhotoPickerDialogFragment : BaseDialogFragment() {

    @InjectSavedState
    private var customFileUri: Uri? = null

    @InjectSavedState
    private var galleryRequestCode: Int = 0
    @InjectSavedState
    private var cameraRequestCode: Int = 0

    override fun onCreateDialog(savedInstanceState: Bundle): Dialog {
        val builder = AlertDialog.Builder(activity)

        builder.setItems(R.array.items_photo_picker, DialogInterface.OnClickListener { dialog, which ->
            val intent: Intent
            val requestCode: Int
            when (which) {
                0    -> {
                    intent = getCameraIntent(customFileUri)
                    requestCode = cameraRequestCode
                }
                1    -> {
                    intent = galleryIntent
                    requestCode = galleryRequestCode
                }
                else -> throw IllegalStateException("Unknown button clicked!")
            }
            processIntent(intent, requestCode)
        })

        return builder.create()
    }

    private fun processIntent(intent: Intent, requestCode: Int) {
        val context = activity
        val resInfoList = context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        if (!resInfoList.isEmpty()) {
            resInfoList
                .map { it.activityInfo.packageName }
                .forEach { context.grantUriPermission(it, customFileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION) }

            if (null == targetFragment) {
                activity.startActivityForResult(intent, requestCode)
            } else {
                targetFragment.startActivityForResult(intent, requestCode)
            }
        }
    }

    class Builder(customFileUri: Uri) {

        private val mDialog: PhotoPickerDialogFragment = PhotoPickerDialogFragment()

        init {
            mDialog.customFileUri = customFileUri
        }

        fun requestCodes(galleryRequestCode: Int, cameraRequestCode: Int): Builder {
            mDialog.galleryRequestCode = galleryRequestCode
            mDialog.cameraRequestCode = cameraRequestCode
            return this
        }

        fun build(): PhotoPickerDialogFragment {
            return mDialog
        }
    }

    companion object {

        fun getCameraIntent(customFileUri: Uri?): Intent {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (null != customFileUri) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, customFileUri)
            }
            return intent
        }

        val galleryIntent: Intent
            get() {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                return intent
            }
    }
}