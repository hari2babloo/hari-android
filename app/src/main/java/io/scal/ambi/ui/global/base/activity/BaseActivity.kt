package io.scal.ambi.ui.global.base.activity

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.disposables.CompositeDisposable
import io.scal.ambi.BR
import io.scal.ambi.R
import io.scal.ambi.ui.global.base.LocalCiceroneHolderViewModel
import io.scal.ambi.ui.global.base.LocalNavigationHolder
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.SupportAppNavigator
import javax.inject.Inject
import javax.inject.Named
import kotlin.reflect.KClass

abstract class BaseActivity<IViewModel : BaseViewModel, Binding : ViewDataBinding> :
    AppCompatActivity(),
    HasSupportFragmentInjector,
    LocalNavigationHolder {

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    internal lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    private lateinit var lifecycleRegistry: LifecycleRegistry

    protected val destroyDisposables: CompositeDisposable = CompositeDisposable()

    protected abstract val layoutId: Int
    protected lateinit var binding: Binding

    protected abstract val viewModelClass: KClass<IViewModel>
    protected val viewModel: IViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(viewModelClass.java)
    }

    @field:Named("rootNavigationHolder")
    @Inject
    internal lateinit var navigationHolder: NavigatorHolder
    @Inject
    internal lateinit var router: Router
    private val localCiceroneViewModel: LocalCiceroneHolderViewModel by lazy {
        ViewModelProviders.of(this).get(LocalCiceroneHolderViewModel::class.java)
    }
    protected open val navigator: Navigator? by lazy {
        object : SupportAppNavigator(this, R.id.container) {

            override fun createActivityIntent(screenKey: String, data: Any?): Intent? = null

            override fun createFragment(screenKey: String, data: Any?): Fragment? = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        lifecycleRegistry = LifecycleRegistry(this)

        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.setVariable(BR.viewModel, viewModel)
    }

    override fun onResumeFragments() {
        super.onResumeFragments()

        navigationHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigationHolder.removeNavigator()

        super.onPause()
    }

    override fun onBackPressed() {
        if (!viewModel.onBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentDispatchingAndroidInjector

    override fun getNavigationHolder(tag: String): NavigatorHolder =
        localCiceroneViewModel.getNavigationHolder(tag, router)

    override fun getRouter(tag: String): Router =
        localCiceroneViewModel.getRouter(tag, router)
}