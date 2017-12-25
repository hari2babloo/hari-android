package io.scal.ambi.ui.global.picker

import android.content.Intent
import android.support.v4.app.DialogFragment

interface PickerViewController {

    fun setPickedImage(fileResource: FileResource)

    fun showPickerDialogFragment(dialogFragment: DialogFragment)

    fun startActivityForResult(intent: Intent, requestCode: Int)

    fun askForReadExternalStoragePermission()
}