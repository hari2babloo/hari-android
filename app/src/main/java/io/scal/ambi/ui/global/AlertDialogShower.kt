package io.scal.ambi.ui.global

import android.app.Activity
import android.support.v7.app.AlertDialog
import javax.inject.Inject

class AlertDialogShower @Inject constructor(private val activity: Activity) {

    fun showMessage(title: String, description: String, positiveButton: String, action: (() -> Unit)? = null) {
        AlertDialog.Builder(activity)
            .run {
                setTitle(title)
                setMessage(description)
                setPositiveButton(positiveButton, { _, _ -> action?.invoke() })
            }
            .show()
    }
}