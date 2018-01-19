package io.scal.ambi.ui.profile.resume

internal sealed class ProfileResumeProgressState {

    object TotalProgress : ProfileResumeProgressState()

    object NoProgress : ProfileResumeProgressState()
}

internal sealed class ProfileResumeErrorState {

    object NoErrorState : ProfileResumeErrorState()

    class FatalErrorState(val error: String) : ProfileResumeErrorState()

    class NonFatalErrorState(val error: String) : ProfileResumeErrorState()
}

sealed class ProfileResumeDataState(open val uiUserResume: UIUserResume) {

    data class ResumeInfo(override val uiUserResume: UIUserResume) : ProfileResumeDataState(uiUserResume)
}