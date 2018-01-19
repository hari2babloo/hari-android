package io.scal.ambi.ui.global.base.fragment

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ambi.work.BR
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.disposables.CompositeDisposable
import io.scal.ambi.extensions.ContextLeakHelper
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import javax.inject.Inject
import javax.inject.Named
import kotlin.reflect.KClass

abstract class BaseFragment<IViewModel : BaseViewModel, Binding : ViewDataBinding> : Fragment(), HasSupportFragmentInjector {

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory
    @field:Named("localNavigationHolder")
    @Inject
    internal lateinit var navigationHolder: NavigatorHolder

    @Inject
    internal lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    private lateinit var lifecycleRegistry: LifecycleRegistry

    protected val destroyViewDisposables: CompositeDisposable = CompositeDisposable()
    protected val destroyFragmentDisposables: CompositeDisposable = CompositeDisposable()

    protected abstract val layoutId: Int
    protected lateinit var binding: Binding

    protected abstract val viewModelClass: KClass<IViewModel>
    protected val viewModel: IViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(viewModelClass.java)
    }
    protected open val navigator: Navigator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleRegistry = LifecycleRegistry(this)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.setVariable(BR.viewModel, viewModel)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        if (null != navigator) {
            navigationHolder.setNavigator(navigator)
        }
    }

    override fun onPause() {
        if (null != navigator) {
            navigationHolder.removeNavigator()
        }

        super.onPause()
    }

    override fun onDestroyView() {
        destroyViewDisposables.clear()

        super.onDestroyView()

        ContextLeakHelper.cleanLeak(this)
    }

    override fun onDestroy() {
        destroyFragmentDisposables.clear()

        super.onDestroy()
    }

    open fun onBackPressed(): Boolean =
        if (null == view) {
            false
        } else {
            viewModel.onBackPressed()
        }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentDispatchingAndroidInjector
}