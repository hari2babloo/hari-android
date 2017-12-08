package io.scal.ambi.ui.global.base.fragment

import android.arch.lifecycle.ViewModelProviders
import android.databinding.ViewDataBinding
import io.scal.ambi.ui.global.base.LocalCiceroneHolderViewModel
import io.scal.ambi.ui.global.base.LocalNavigationHolder
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import javax.inject.Inject

abstract class BaseNavigationFragment<IViewModel : BaseViewModel, Binding : ViewDataBinding> :
    BaseFragment<IViewModel, Binding>(),
    LocalNavigationHolder {

    @Inject
    internal lateinit var router: Router

    private val localCiceroneViewModel: LocalCiceroneHolderViewModel by lazy {
        ViewModelProviders.of(this).get(LocalCiceroneHolderViewModel::class.java)
    }

    override fun getNavigationHolder(tag: String): NavigatorHolder =
        localCiceroneViewModel.getNavigationHolder(tag, router)

    override fun getRouter(tag: String): Router =
        localCiceroneViewModel.getRouter(tag, router)
}