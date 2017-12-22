package io.scal.ambi.ui.global.picker

import android.app.DialogFragment
import android.content.Intent

interface PickerViewController {

    abstract fun setPickedImage(photoResource: PhotoResource?)

    abstract fun showPickerDialogFragment(dialogFragment: DialogFragment)

    abstract fun startPickerActivity(intent: Intent, requestCode: Int)

    abstract fun askForReadExternalStoragePermission()
}