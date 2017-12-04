package io.scal.ambi.ui.global

import android.app.Fragment
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasFragmentInjector
import io.reactivex.disposables.CompositeDisposable
import io.scal.ambi.BR
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import javax.inject.Inject
import kotlin.reflect.KClass


abstract class BaseActivity<IViewModel : BaseViewModel, Binding : ViewDataBinding> : AppCompatActivity(), HasFragmentInjector {

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    internal lateinit var navigationHolder: NavigatorHolder

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
    protected open val navigator: Navigator? = null

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

    override fun fragmentInjector(): AndroidInjector<Fragment> = fragmentDispatchingAndroidInjector
}