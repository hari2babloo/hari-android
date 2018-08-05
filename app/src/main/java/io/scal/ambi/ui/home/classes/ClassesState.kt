package io.scal.ambi.ui.home.classes

import android.databinding.ObservableList

internal sealed class ClassesState {

    object NoProgress : ClassesState()

    object EmptyProgress : ClassesState()

    object RefreshProgress : ClassesState()

    object PageProgress : ClassesState()

    object TotalProgress : ClassesState()
}

internal sealed class ClassesErrorState {



    object NoErrorState : ClassesErrorState()

    class FatalErrorState(val error: String) : ClassesErrorState()

    class NonFatalErrorState(val error: String) : ClassesErrorState()
}

sealed class ClassesDataState(open val profileInfo: ClassesData?) {

    data class DataInfoOnly(override val profileInfo: ClassesData) : ClassesDataState(profileInfo)

    data class ClassesEmpty(override val profileInfo: ClassesData?) : ClassesDataState(profileInfo)

    data class ClassesFeed(override val profileInfo: ClassesData?, val newsFeed: ObservableList<ClassesData>) : ClassesDataState(profileInfo)

    fun copyNotificationInfo(uiProfile: ClassesData): ClassesDataState =
            when (this) {
                is DataInfoOnly      -> copy(profileInfo = uiProfile)
                is ClassesEmpty -> copy(profileInfo = uiProfile)
                is ClassesFeed -> copy(profileInfo = uiProfile)
            }
}