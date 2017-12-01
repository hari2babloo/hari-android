package io.scal.ambi.ui.auth.login

import android.content.Context
import android.content.Intent
import io.scal.ambi.R
import io.scal.ambi.databinding.ActivityLoginBinding
import io.scal.ambi.presentation.auth.LoginViewModel
import io.scal.ambi.ui.global.BaseActivity
import kotlin.reflect.KClass

class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>() {

    override val layoutId: Int = R.layout.activity_login
    override val viewModelClass: KClass<LoginViewModel> = LoginViewModel::class

    companion object {

        fun createScreen(context: Context) =
                Intent(context, LoginActivity::class.java)
    }
}