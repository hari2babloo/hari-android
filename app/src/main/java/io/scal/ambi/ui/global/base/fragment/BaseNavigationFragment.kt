package io.scal.ambi.ui.global.base.fragment

import android.arch.lifecycle.ViewModelProviders
import android.databinding.ViewDataBinding
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.base.LocalCiceroneHolderViewModel
import io.scal.ambi.ui.global.base.LocalNavigationHolder
import io.scal.ambi.ui.global.base.activity.BaseNavigator
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import javax.inject.Inject

abstract class BaseNavigationFragment<IViewModel : BaseViewModel, Binding : ViewDataBinding> :
    BaseFragment<IViewModel, Binding>(),
    LocalNavigationHolder {

    @Inject
    internal lateinit var router: BetterRouter

    private val localCiceroneViewModel: LocalCiceroneHolderViewModel by lazy {
        ViewModelProviders.of(this).get(LocalCiceroneHolderViewModel::class.java)
    }

    override fun getNavigationHolder(tag: String): NavigatorHolder =
        localCiceroneViewModel.getNavigationHolder(tag, router)

    override fun getRouter(tag: String): BetterRouter =
        localCiceroneViewModel.getRouter(tag, router)

    override val navigator: Navigator?
        get() = BaseNavigator(this)
}