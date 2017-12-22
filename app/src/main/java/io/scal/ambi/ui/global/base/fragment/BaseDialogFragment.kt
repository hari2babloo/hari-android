package io.scal.ambi.ui.global.base.fragment

import android.app.DialogFragment
import android.os.Bundle
import com.azoft.injectorlib.Injector

abstract class BaseDialogFragment : DialogFragment() {

    private val mInjector = Injector.init(javaClass)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mInjector.applyRestoreInstanceState(this, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mInjector.applyOnSaveInstanceState(this, outState)
    }
}
