package io.scal.ambi.ui.launcher

import android.content.Context
import android.os.Bundle
import io.scal.ambi.ui.global.BaseActivity
import javax.inject.Inject

class LauncherActivity : BaseActivity() {

    @Inject
    lateinit var cds: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}