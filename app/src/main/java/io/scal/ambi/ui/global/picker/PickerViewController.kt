package io.scal.ambi.ui.global.picker

import android.content.Intent
import android.support.v4.app.DialogFragment

interface PickerViewController {

    fun setPickedFile(fileResource: FileResource, image: Boolean)

    fun showPickerDialogFragment(dialogFragment: DialogFragment)

    fun startActivityForResult(intent: Intent, requestCode: Int)

    fun askForReadExternalStoragePermission()
}