package io.scal.ambi.ui.global

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

object KeyboardUtils {

    fun hideSoftKeyboard(context: Context) {
        if (context is Activity) {
            hideSoftKeyboard(context, false, context.window.decorView)
        }
    }

    fun hideSoftKeyboard(context: Context, vararg views: View?) {
        hideSoftKeyboard(context, true, *views)
    }

    private fun hideSoftKeyboard(context: Context, clearFocus: Boolean, vararg views: View?) {
        val manager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        for (currentView in views) {
            if (null == currentView) {
                continue
            }
            if (clearFocus) {
                currentView.clearFocus()
            }
            manager.hideSoftInputFromWindow(currentView.windowToken, 0)
            manager.hideSoftInputFromWindow(currentView.applicationWindowToken, 0)
        }
    }

    fun showSoftKeyboard(context: Context, view: View) {
        val manager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view.requestFocus()
        manager.showSoftInput(view, 0)
    }
}