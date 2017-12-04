package io.scal.ambi.ui.home

import android.content.Context
import android.content.Intent
import io.scal.ambi.R
import io.scal.ambi.databinding.ActivityHomeBinding
import io.scal.ambi.presentation.home.root.HomeViewModel
import io.scal.ambi.ui.global.BaseActivity
import kotlin.reflect.KClass

class HomeActivity : BaseActivity<HomeViewModel, ActivityHomeBinding>() {

    override val layoutId: Int = R.layout.activity_home
    override val viewModelClass: KClass<HomeViewModel> = HomeViewModel::class

    companion object {

        fun createScreen(context: Context) =
            Intent(context, HomeActivity::class.java)
    }
}