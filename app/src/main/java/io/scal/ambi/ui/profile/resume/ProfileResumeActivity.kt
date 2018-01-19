package io.scal.ambi.ui.profile.resume

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.ambi.work.R
import com.ambi.work.databinding.ActivityProfileResumeBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.ToolbarType
import io.scal.ambi.ui.auth.profile.AuthProfileCheckerViewModel
import io.scal.ambi.ui.global.base.ErrorState
import io.scal.ambi.ui.global.base.activity.BaseNavigator
import io.scal.ambi.ui.global.base.activity.BaseToolbarActivity
import io.scal.ambi.ui.global.base.asErrorState
import ru.terrakok.cicerone.Navigator
import kotlin.reflect.KClass

class ProfileResumeActivity : BaseToolbarActivity<ProfileResumeViewModel, ActivityProfileResumeBinding>() {

    override val layoutId: Int = R.layout.activity_profile_resume
    override val viewModelClass: KClass<ProfileResumeViewModel> = ProfileResumeViewModel::class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initToolbar()
        observeStates()

        ViewModelProviders.of(this, viewModelFactory).get(AuthProfileCheckerViewModel::class.java)
    }

    private fun initToolbar() {
        setToolbarType(ToolbarType(IconImage(R.drawable.ic_back),
                                   Runnable { viewModel.onBackPressed() },
                                   ToolbarType.TitleContent(getString(R.string.title_profile_resume)),
                                   null,
                                   null))
    }

    private fun observeStates() {
        viewModel.progressState
            .toObservable()
            .distinctUntilChanged()
            .subscribe({
                           when (it) {
                               ProfileResumeProgressState.NoProgress -> binding.progress.visibility = View.GONE
                               ProfileResumeProgressState.NoProgress -> binding.progress.visibility = View.VISIBLE
                           }
                       }
            )
            .addTo(destroyDisposables)

        viewModel.errorState
            .asErrorState(binding.rootContainer,
                          { viewModel.loadResumeInfo() },
                          {
                              when (it) {
                                  is ProfileResumeErrorState.NoErrorState       -> ErrorState.NoError
                                  is ProfileResumeErrorState.NonFatalErrorState -> ErrorState.NonFatalError(it.error)
                                  is ProfileResumeErrorState.FatalErrorState    -> ErrorState.FatalError(it.error)
                              }
                          },
                          destroyDisposables)

        viewModel.dataState
            .toObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                when (it) {
                    is ProfileResumeDataState.ResumeInfo -> {
                    }
                }
            }
            .addTo(destroyDisposables)

        viewModel
            .currentUser
            .toObservable()
            .map { it.name }
            .distinctUntilChanged()
            .subscribe { binding.tvUserName.text = it }
            .addTo(destroyDisposables)
    }

    override val navigator: Navigator
        get() = object : BaseNavigator(this) {}

    companion object {

        fun createScreen(context: Context): Intent =
            Intent(context, ProfileResumeActivity::class.java)
    }
}