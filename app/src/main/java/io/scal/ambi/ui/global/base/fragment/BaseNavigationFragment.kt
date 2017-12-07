package io.scal.ambi.ui.global.base.fragment

import android.databinding.ViewDataBinding
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import ru.terrakok.cicerone.NavigatorHolder
import javax.inject.Inject
import javax.inject.Named

abstract class BaseNavigationFragment<IViewModel : BaseViewModel, Binding : ViewDataBinding> : BaseFragment<IViewModel, Binding>() {

    @Named("localNavigationHolder")
    @Inject
    override lateinit var navigationHolder: NavigatorHolder
}