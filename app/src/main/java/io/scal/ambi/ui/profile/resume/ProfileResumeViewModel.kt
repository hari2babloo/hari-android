package io.scal.ambi.ui.profile.resume

import android.content.Context
import android.databinding.ObservableField
import io.scal.ambi.entity.user.User
import io.scal.ambi.entity.user.UserResume
import io.scal.ambi.extensions.binding.observable.ObservableString
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.interactor.profile.IProfileResumeInteractor
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.base.viewmodel.BaseUserViewModel
import io.scal.ambi.ui.global.base.viewmodel.toGoodUserMessage
import javax.inject.Inject

class ProfileResumeViewModel @Inject internal constructor(private val context: Context,
                                                          router: BetterRouter,
                                                          private val interactor: IProfileResumeInteractor,
                                                          rxSchedulersAbs: RxSchedulersAbs) :
    BaseUserViewModel(router, { interactor.loadCurrentUser() }, rxSchedulersAbs) {

    internal val progressState = ObservableField<ProfileResumeProgressState>(ProfileResumeProgressState.TotalProgress)
    internal val errorState = ObservableField<ProfileResumeErrorState>()
    val dataState = ObservableField<ProfileResumeDataState>()

    override fun onCurrentUserFetched(user: User) {
        super.onCurrentUserFetched(user)

        loadResumeInfo()
    }

    fun loadResumeInfo() {
        interactor.loadResumeInfo(currentUser.get().uid)
            .compose(rxSchedulersAbs.getIOToMainTransformerSingle())
            .subscribe({
                           dataState.set(ProfileResumeDataState.ResumeInfo(it.toResumeInfo()))
                       },
                       {
                           handleError(it)

                           errorState.set(ProfileResumeErrorState.FatalErrorState(it.toGoodUserMessage(context)))
                       })
    }

    fun editPitch() {
        val currentData = dataState.get()
        if (currentData is ProfileResumeDataState.ResumeInfo) {
            val newResume = currentData.uiUserResume.copy(pitchEditing = !currentData.uiUserResume.pitchEditing)
            dataState.set(currentData.copy(uiUserResume = newResume))
        }
    }
}

private fun UserResume.toResumeInfo(): UIUserResume {
    return UIUserResume(pitch, ObservableString(pitch), false)
}
